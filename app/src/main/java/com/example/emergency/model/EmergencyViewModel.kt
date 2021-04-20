package com.example.emergency.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _status = MutableLiveData(STATUS.Call.INIT)
    val status: LiveData<STATUS.Call> = _status

    private val _currentText = MutableLiveData<String>()
    val currentText: LiveData<String> = _currentText

    private var mLocationClient = AMapLocationClient(applicationContext)

    private var callId: String? = null
    lateinit var errorMessage: String


    private lateinit var chosen: Info

    private var locationGetSuccess = false
    private var checked = false

    private var job: Job? = null

    private var aMapLocationListener = AMapLocationListener {
        if (it != null) {
            if (it.errorCode == 0) {
                _currentText.value = "正在为${chosen.realName}呼救\n" +
                        "获取位置完成"
                submitLocation(it)
                locationGetSuccess = true
                if (checked) {
                    setStatus(STATUS.Call.COMPLETE)
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
                if (it.isEmpty()) {
                    _currentText.value = "请添加呼救人"
                } else {
                    chosen = it[0]
                    _currentText.value = "点击为${chosen.realName}呼救"
                }
            }
        }
    }


    fun initLiveData() {
        liveQueryRepository.init()
    }

    fun unsubscribe() {
        liveQueryRepository.unsubscribe()
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                infoRepository.refreshInfo()
                historyRepository.refreshHistory()
                setStatus(STATUS.Call.INIT)
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                setStatus(STATUS.Call.ERROR)
            }

        }
    }

    fun getStatus(): STATUS.Call {
        return status.value!!
    }

    fun setStatus(status: STATUS.Call) {
        _status.value = status
        when (status) {
            STATUS.Call.INIT -> {
                locationGetSuccess = false
                checked = false
                callId = null
                job?.cancel()
            }
            // 获取位置
            STATUS.Call.GET_LOCATION -> {
                viewModelScope.launch {
                    _currentText.value = "正在为${chosen.realName}呼救\n" +
                            "获取位置中..."
                    getCurrentLocation()
                }
            }
            STATUS.Call.CALLING -> {
                viewModelScope.launch {
                    _currentText.value = "正在为${chosen.realName}呼救\n" +
                            "创建呼救中..."
                    submitCall()
                }
            }
            STATUS.Call.CANCEL -> {
                mLocationClient.stopLocation()
                if (checked) {
                    _currentText.value = "取消提交失败，当前请求已处理\n" +
                            "点击以重新呼救"
                    setStatus(STATUS.Call.INIT)
                    return
                }

                if (callId != null) {
                    viewModelScope.launch {
                        try {
                            emergencyRepository.setStatus(callId!!, "已取消")
                            _currentText.value = "已取消，再次点击以呼救"
                            setStatus(STATUS.Call.INIT)
                        } catch (e: Exception) {
                            _currentText.value = "取消失败，请检查网络连接"
                        }
                    }
                } else {
                    _currentText.value = "已取消，再次点击以呼救"
                    setStatus(STATUS.Call.INIT)
                }
            }
            STATUS.Call.COMPLETE -> {
                _currentText.value = "呼叫已处理，点击以重新呼救"
                setStatus(STATUS.Call.INIT)
            }
            STATUS.Call.ERROR -> {
                viewModelScope.launch {
                    try {
                        delay(5000) // 每五秒重试连接
                        infoRepository.refreshInfo()
                        historyRepository.refreshHistory()
                        initLiveData()
                        setStatus(STATUS.Call.INIT)
                    } catch (e: Exception) {
                        errorMessage = getErrorMessage(e)
                        setStatus(STATUS.Call.ERROR)
                    }
                }
            }
        }
    }

    private fun submitCall() {
        viewModelScope.launch {
            val call = Call(
                patientName = chosen.realName,
                patientId = chosen.id,  // 待修改
            )
            try {
                callId = emergencyRepository.submitOneCall(call)
                setStatus(STATUS.Call.GET_LOCATION) //前往位置获取
                job = viewModelScope.launch {
                    emergencyRepository.getStatus(callId!!).collect {
                        if (it.isNotEmpty()) {
                            if (it[0].status == "已处理") {
                                checked = true
                                if (!locationGetSuccess) {
                                    _currentText.value = "位置尚未获取完成，正在获取"
                                } else {
                                    setStatus(STATUS.Call.COMPLETE)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _currentText.value = "呼救提交失败，请检查网络连接"
                setStatus(STATUS.Call.INIT)
            }
        }
    }

    private fun submitLocation(location: AMapLocation) {
        viewModelScope.launch {
            try {
                emergencyRepository.submitPosition(
                    callId!!,
                    Location(
                        name = location.address,
                        coordinate = "${location.latitude} ${location.longitude}"
                    )
                )
                _currentText.value = "位置信息已提交, 等待处理..."
            } catch (e: Exception) {
                _currentText.value = "位置信息提交失败，请检查网络连接"
            }
        }
    }


    private fun getCurrentLocation() {
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