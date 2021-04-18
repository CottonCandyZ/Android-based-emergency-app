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
import com.example.emergency.data.local.repository.EmergencyRepository
import com.example.emergency.data.local.repository.HistoryRepository
import com.example.emergency.data.local.repository.InfoRepository
import com.example.emergency.data.local.repository.LiveQueryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private lateinit var chosen: Info
    private var aMapLocationListener = AMapLocationListener {
        if (it != null) {
            if (it.errorCode == 0) {
                _currentText.value = "正在为${chosen.realName}呼救\n" +
                        "获取位置完成"
                location = it
                setState(STATUS.Call.CALLING)
            } else {
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
                setState(STATUS.Call.INIT)
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
            infoRepository.refreshInfo()
        }
        viewModelScope.launch {
            historyRepository.refreshHistory()
        }
    }


    private lateinit var location: AMapLocation


    fun setState(status: STATUS.Call) {
        when (status) {
            STATUS.Call.INIT -> {

            }
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
                    submit()
                    _currentText.value = "正在为${chosen.realName}呼救\n" +
                            "呼救已提交，等待回应..."
                }

            }
            STATUS.Call.CANCEL -> {
                mLocationClient.stopLocation()
                _currentText.value = "已取消，再次点击以呼救"
            }
        }
        _status.value = status
    }

    fun getStatus(): STATUS.Call {
        return status.value!!
    }

    private fun submit() {
        val call = Call(
            locationCoordinate = "${location.latitude} ${location.longitude}",
            locationName = location.address,
            patientName = chosen.realName,
            patientId = chosen.id,  // 待修改
        )
        viewModelScope.launch {
            try {
                emergencyRepository.submitOneCall(call)
            } catch (e: Exception) {

            }

        }

    }

    private suspend fun getCurrentLocation() =
        withContext(Dispatchers.IO) {
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
            mLocationClient.startLocation()
        }

}