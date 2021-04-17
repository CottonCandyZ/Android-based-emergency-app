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
import com.example.emergency.data.Resource
import com.example.emergency.data.entity.*
import com.example.emergency.data.local.repository.InfoRepository
import com.example.emergency.data.local.repository.UserRepository
import com.example.emergency.data.succeeded
import com.example.emergency.ui.info.InputHint
import com.example.emergency.util.ID_NOT_FOUND_ERROR
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


const val INPUT_ARRAY_SIZE = 11

enum class InfoState {
    SHOW, NEW, EDIT
}

enum class CALL_STATUS {
    INIT, CALLING, CANCEL, GET_LOCATION
}


@HiltViewModel
class MyViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
    private val userRepository: UserRepository,
    @ApplicationContext applicationContext: Context
) : ViewModel() {
    // 用于保存填入的信息
    lateinit var inputData: InputData
    private lateinit var emergencyContactsCopy: ArrayList<EmergencyContact>
    private lateinit var infoWithEmergencyContact: InfoWithEmergencyContact
    private var _lastCheckedInfo = MutableLiveData<AbstractInfo>()
    val lastCheckedInfo: LiveData<AbstractInfo> = _lastCheckedInfo

    private val _showInfo = MutableLiveData<Resource<InputData>>()
    val showInfo: LiveData<Resource<InputData>> = _showInfo


    private val _user = MutableLiveData<Resource<User>>()
    val user: LiveData<Resource<User>> = _user

    // 页面跳转时的 ID
    var showInfoId: String? = null


    // info 页面数据
    private val _infoFragmentTitle = MutableLiveData<String>()
    val infoFragmentTitle: LiveData<String> = _infoFragmentTitle


    // 我的页面数据
    private val _abstractInfo = MutableLiveData<Resource<List<AbstractInfo>>>()
    val abstractInfo: LiveData<Resource<List<AbstractInfo>>> = _abstractInfo
    var shouldUpdate = true


    private val _infoState = MutableLiveData<InfoState>()
    val infoState: LiveData<InfoState> = _infoState


    // 第一次进入页面时需要从远程获取数据
    init {
        viewModelScope.launch {
            _user.value = userRepository.getCurrentUser()
            fetchAbstractInfo(true)
            _lastCheckedInfo.value = abstractInfo.value?.data!!.first { it.chosen }
            mLocationClient.setLocationListener(aMapLocationListener)
            setState(CALL_STATUS.INIT)
        }
    }

    fun changeInfoState(infoState: InfoState) {
        when (infoState) {
            InfoState.NEW -> {
                inputData =
                    InputData(Array(INPUT_ARRAY_SIZE) { "" }, arrayListOf(EmergencyContact()))
            }
            InfoState.SHOW -> {
                inputData = InputData(Array(INPUT_ARRAY_SIZE) { "" }, arrayListOf())
            }
            InfoState.EDIT -> {
                inputData.emergencyNumber.add(EmergencyContact())
            }
        }
        _infoState.value = infoState
    }

    fun changeInfoTitle(title: String) {
        _infoFragmentTitle.value = title
    }

    // 获取我的页面的数据
    suspend fun fetchAbstractInfo(remote: Boolean) {
        _abstractInfo.value = infoRepository.getAbstractInfo(remote)
    }

    // 获取详细信息的数据
    suspend fun fetchInfo() {
        emergencyContactsCopy = arrayListOf()

        val result = infoRepository.getInfo(showInfoId!!)
        if (result is Resource.Error && result.message == ID_NOT_FOUND_ERROR) {
            _showInfo.value = Resource.Error(result.message)
            return
        }
        infoWithEmergencyContact = result.data!![0]
        val info = infoWithEmergencyContact.info
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
        val inputInfo = inputData.inputInfo
        info.run {
            with(InputHint) {
                inputInfo[REAL_NAME] = realName
                inputInfo[SEX] = sex
                inputInfo[BIRTHDATE] = simpleDateFormat.format(birthdate)
                inputInfo[PHONE] = phone
                inputInfo[WEIGHT] = weight.toString()
                inputInfo[BLOOD_TYPE] = bloodType
                inputInfo[MEDICAL_CONDITIONS] = medicalConditions
                inputInfo[MEDICAL_NOTES] = medicalConditions
                inputInfo[ALLERGY] = allergy
                inputInfo[MEDICATIONS] = medications
                inputInfo[ADDRESS] = address
            }
        }
        val emergencyContacts = infoWithEmergencyContact.emergencyContacts
        emergencyContacts.forEach {
            inputData.emergencyNumber.add(it)
        }
        emergencyContactsCopy.addAll(emergencyContacts)
        _showInfo.value =
            if (result.succeeded) {
                Resource.Success(inputData)
            } else {
                Resource.Error(result.message!!, inputData)
            }
    }

    suspend fun deleteInfoWithEmergencyContact() {
        infoRepository.deleteInfoWithEmergencyContact(infoWithEmergencyContact)
    }


    fun updateAbstractInfo(update: AbstractInfo) {
        viewModelScope.launch {
            val remove = lastCheckedInfo.value!!.copy()
            remove.chosen = false
            infoRepository.updateItemChosen(remove, update)


//            shouldUpdate = !(infoRepository.updateItemChosen(remove) && infoRepository.updateItemChosen(update))
//            if (shouldUpdate) {
//                _abstractInfo.value =
//                    Resource.Error("该项似乎已被删除", infoRepository.getAbstractInfo(true).data)
//            }
//            shouldUpdate = false
            fetchAbstractInfo(true)
        }
        _lastCheckedInfo.value = update
        if (CallStatus.value == CALL_STATUS.INIT) {
            setState(CALL_STATUS.INIT)
        }
    }

    suspend fun save() {
        // 判断 info 状态
        val saveFromId = _infoState.value != InfoState.NEW
        val infoId = if (saveFromId) showInfoId!! else ""
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
        val inputInfo = inputData.inputInfo
        val date = Date(simpleDateFormat.parse(inputInfo[InputHint.BIRTHDATE])!!.time)
        val weight =
            if (inputInfo[InputHint.WEIGHT] == "") 0 else inputInfo[InputHint.WEIGHT].toInt()
        // 如果是新创建的，则给到
        val chosen =
            if (saveFromId) infoWithEmergencyContact.info.chosen else abstractInfo.value!!.data!!.isEmpty()
        val info = Info(
            id = infoId,
            realName = inputInfo[InputHint.REAL_NAME],
            sex = inputInfo[InputHint.SEX],
            birthdate = date,
            phone = inputInfo[InputHint.PHONE],
            weight = weight,
            bloodType = inputInfo[InputHint.BLOOD_TYPE],
            medicalConditions = inputInfo[InputHint.MEDICAL_CONDITIONS],
            medicalNotes = inputInfo[InputHint.MEDICAL_NOTES],
            allergy = inputInfo[InputHint.ALLERGY],
            medications = inputInfo[InputHint.MEDICATIONS],
            address = inputInfo[InputHint.ADDRESS],
            chosen = chosen,
        )
        val saveList = inputData.emergencyNumber.filter { it.phone != "" }

        if (saveFromId) {
            emergencyContactsCopy.forEach { old ->
                if (!inputData.emergencyNumber.any { it.id == old.id }) {
                    infoRepository.deleteEmergencyContact(old.id)
                }
            }
        }
        val save = InfoWithEmergencyContact(info, saveList)
        infoRepository.saveInfoWithEmergencyContact(
            save,
            saveFromId
        )
    }

    private val _status = MutableLiveData(CALL_STATUS.INIT)
    val CallStatus: LiveData<CALL_STATUS> = _status

    private val _currentText = MutableLiveData<String>()
    val currentText: LiveData<String> = _currentText

    private var mLocationClient = AMapLocationClient(applicationContext)
    private var aMapLocationListener = AMapLocationListener {
        if (it != null) {
            if (it.errorCode == 0) {
                _currentText.value = "正在为${lastCheckedInfo.value!!.realName}呼救\n" +
                        "获取位置完成"
                location = it
                setState(CALL_STATUS.CALLING)
            } else {
                Log.e(
                    "AMapError",
                    "location Error, ErrCode: ${it.errorCode}, errInfo: ${it.errorInfo}"
                )
            }
        }
    }

    private lateinit var location: AMapLocation


    fun setState(CallStatus: CALL_STATUS) {
        when (CallStatus) {
            CALL_STATUS.INIT -> {
                viewModelScope.launch {
                    if (lastCheckedInfo.value == null) {
                        _currentText.value = "请添加呼救人"
                    } else {
                        _currentText.value = "点击为${lastCheckedInfo.value!!.realName}呼救"
                    }
                }
            }
            CALL_STATUS.GET_LOCATION -> {
                if (lastCheckedInfo.value == null) {
                    return
                }
                viewModelScope.launch {
                    _currentText.value = "正在为${lastCheckedInfo.value!!.realName}呼救\n" +
                            "获取位置中..."
                    getCurrentLocation()
                }
            }
            CALL_STATUS.CALLING -> {
                viewModelScope.launch {
                    _currentText.value = "正在为${lastCheckedInfo.value!!.realName}呼救\n" +
                            "创建呼救中..."
                    submit()
                    _currentText.value = "正在为${lastCheckedInfo.value!!.realName}呼救\n" +
                            "呼救已提交，等待回应..."

                }

            }
            CALL_STATUS.CANCEL -> {
                mLocationClient.stopLocation()
                _currentText.value = "已取消，再次点击以呼救"
            }
        }
        _status.value = CallStatus
    }

    private suspend fun submit() {
        val call = Call(
            locationCoordinate = "${location.latitude} ${location.longitude}",
            locationName = location.address,
            patientName = lastCheckedInfo.value!!.realName,
            patientId = lastCheckedInfo.value!!.id,  // 待修改
        )
        infoRepository.submitOneCall(call)
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

class InputData(
    val inputInfo: Array<String>,
    val emergencyNumber: ArrayList<EmergencyContact>
)