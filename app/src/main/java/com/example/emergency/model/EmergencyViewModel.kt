package com.example.emergency.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.leancloud.livequery.AVLiveQuery
import cn.leancloud.livequery.AVLiveQueryConnectionHandler
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.example.emergency.data.entity.Call
import com.example.emergency.data.entity.Info
import com.example.emergency.data.entity.Location
import com.example.emergency.data.local.repository.EmergencyRepository
import com.example.emergency.data.local.repository.HistoryRepository
import com.example.emergency.data.local.repository.InfoRepository
import com.example.emergency.data.local.repository.LiveQueryRepository
import com.example.emergency.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val emergencyRepository: EmergencyRepository,
    private val historyRepository: HistoryRepository,
    private val liveQueryRepository: LiveQueryRepository,
    @ApplicationContext applicationContext: Context
) : ViewModel() {

    private val _state = MutableLiveData(STATE.Call.INIT)
    val state: LiveData<STATE.Call> = _state

    private val _currentText = MutableLiveData<String>()
    val currentText: LiveData<String> = _currentText

    private var mLocationClient = AMapLocationClient(applicationContext)

    private var callId: String? = null
    lateinit var errorMessage: String


    private lateinit var chosen: Info
    lateinit var handlerPhone: String

    private var checked = false

    private var checkedJob: Job? = null
    private var callSubmitJob: Job? = null
    private var locationSubmitJob: Job? = null


    private var location: AMapLocation? = null

    private var locationSendSuccess = false

    private var aMapLocationListener = AMapLocationListener { // 位置回调
        if (it != null) {
            if (it.errorCode == 0) {
                if (getState() != STATE.Call.CALLING) {
                    return@AMapLocationListener
                }
                _currentText.value = "正在为${chosen.realName}呼救\n" +
                        "获取位置完成"
                location = it
                if (!locationSendSuccess) {
                    submitLocation()
                }

                if (checked) {
                    setState(STATE.Call.COMPLETE)
                }
            } else {
                _currentText.value = "获取位置失败，尝试重新获取"
                getCurrentLocation()
                Log.e(
                    "AMapError",
                    "location Error, ErrCode: ${it.errorCode}, errInfo: ${it.errorInfo}"
                )
            }
        }
    }

    init {
        mLocationClient.setLocationListener(aMapLocationListener)
        refresh()
        initLiveData()
        viewModelScope.launch {
            infoRepository.getCurrentChosen().collect {
                if (getState() == STATE.Call.CALLING) {
                    return@collect
                }
                if (it.isEmpty()) {
                    _currentText.value = "请添加呼救人"
                } else {
                    chosen = it[0]
                    _currentText.value = "点击为${chosen.realName}呼救"
                }
            }
        }
        AVLiveQuery.setConnectionHandler(object : AVLiveQueryConnectionHandler {
            override fun onConnectionOpen() {
                initLiveData()
            }

            override fun onConnectionClose() {
            }

            override fun onConnectionError(code: Int, reason: String?) {
            }

        })
    }


    private fun initLiveData() {
        liveQueryRepository.init()
    }

    private fun refresh() {
        viewModelScope.launch {
            try {
                infoRepository.refreshInfo()
                historyRepository.refreshHistory()
                setState(STATE.Call.INIT)
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                setState(STATE.Call.ERROR)
            }

        }
    }

    fun getState(): STATE.Call {
        return state.value!!
    }

    fun setState(state: STATE.Call) {
        _state.value = state
        when (state) {
            STATE.Call.INIT -> {
                locationSendSuccess = false
                location = null
                checked = false
                callId = null
                checkedJob?.cancel()
                callSubmitJob = null
                locationSubmitJob = null
            }
            STATE.Call.CALLING -> {
                _currentText.value = "正在为${chosen.realName}呼救\n" +
                        "创建呼救中..."
                submitCall()
                getCurrentLocation()
            }
            STATE.Call.CANCEL -> {
                mLocationClient.stopLocation()
                locationSubmitJob?.cancel()
                if (checked) { // 若请求已经被处理
                    _currentText.value = "取消提交失败，当前请求已处理\n" +
                            "点击以重新呼救"
                    setState(STATE.Call.INIT)
                    return
                }
                if (callId != null) { // 若已提交
                    viewModelScope.launch {
                        try {
                            checkedJob?.cancel()
                            emergencyRepository.setStatus(callId!!, "已取消")
                            _currentText.value = "已取消，点击以为${chosen.realName}呼救"
                            setState(STATE.Call.INIT)
                        } catch (e: Exception) {
                            _currentText.value = "取消失败，请检查网络连接"
                        }
                    }
                } else { // 若尚未提交
                    callSubmitJob?.cancel()
                    _currentText.value = "已取消，再次点击以呼救"
                    setState(STATE.Call.INIT)
                }
            }
            STATE.Call.COMPLETE -> {
                _currentText.value = "呼叫已处理，点击以重新呼救"
                setState(STATE.Call.INIT)
            }
            STATE.Call.ERROR -> {
                viewModelScope.launch {
                    try {
                        delay(5000) // 每五秒重试连接
                        infoRepository.refreshInfo()
                        historyRepository.refreshHistory()
                        initLiveData()
                        setState(STATE.Call.INIT)
                    } catch (e: Exception) {
                        errorMessage = getErrorMessage(e)
                        setState(STATE.Call.ERROR)
                    }
                }
            }
        }

    }

    private fun submitCall() { // 提交呼救
        callSubmitJob = viewModelScope.launch {
            val call = Call(
                patientName = chosen.realName,
                patientId = chosen.id,
            )
            try {
                callId = emergencyRepository.submitOneCall(call)
                if (location != null && !locationSendSuccess) {
                    submitLocation()
                } else {
                    _currentText.value = "正在为${chosen.realName}呼救\n" +
                            "呼救已提交，正在等待位置获取"
                }
                checkedJob = viewModelScope.launch check@{ // 检查呼救状态
                    if (callId == null) {
                        return@check
                    }
                    emergencyRepository.getStatus(callId!!).collect {
                        if (it.isNotEmpty()) {
                            if (it[0].status == "已处理") {
                                checked = true
                                if (location == null) {
                                    _currentText.value = "正在为${chosen.realName}呼救\n" +
                                            "位置尚未获取完成，正在获取"
                                } else {
                                    handlerPhone = it[0].handlerPhone!!
                                    setState(STATE.Call.COMPLETE)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    _currentText.value = "呼救提交失败，请检查网络连接"
                    mLocationClient.stopLocation()
                    setState(STATE.Call.INIT)
                }
            }
        }
    }

    private fun submitLocation() { // 提交位置
        locationSubmitJob = viewModelScope.launch {
            try {
                if (callId == null || location == null) {
                    return@launch
                }
                emergencyRepository.submitPosition(
                    callId!!,
                    Location(
                        name = location!!.address,
                        coordinate = "${location!!.latitude} ${location!!.longitude}"
                    )
                )
                _currentText.value = "正在为${chosen.realName}呼救\n" +
                        "位置信息已提交, 等待处理..."
                locationSendSuccess = true
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    _currentText.value = "位置信息提交失败，请检查网络连接\n" +
                            "正在重试"
                    submitLocation()
                }

            }
        }
    }


    private fun getCurrentLocation() { // 获取位置
        val mLocationOption = AMapLocationClientOption()
        val option = AMapLocationClientOption()
        option.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn
        mLocationClient.setLocationOption(option)
        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        mLocationClient.stopLocation()
        mLocationClient.startLocation()
        // 定位模式：高精度
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        // 仅获取一次位置
        mLocationOption.isOnceLocationLatest = true
        // 请求超时时间 ms
        mLocationOption.httpTimeOut = 20000
        mLocationOption.isMockEnable = true
        mLocationClient.setLocationOption(option)
        mLocationClient.stopLocation()
        mLocationClient.startLocation()
    }
}