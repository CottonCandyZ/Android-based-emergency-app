package com.example.emergency.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergency.data.Resource
import com.example.emergency.data.entity.AbstractInfo
import com.example.emergency.data.entity.EmergencyContact
import com.example.emergency.data.entity.Info
import com.example.emergency.data.entity.InfoWithEmergencyContact
import com.example.emergency.data.local.InfoRepository
import com.example.emergency.data.succeeded
import com.example.emergency.ui.info.InputHint
import com.example.emergency.util.ID_NOT_FOUND_ERROR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


const val INPUT_ARRAY_SIZE = 11

enum class InfoState {
    SHOW, NEW, EDIT
}

@HiltViewModel
class MyViewModel @Inject constructor(
    private val infoRepository: InfoRepository,
) : ViewModel() {
    // 用于保存填入的信息
    lateinit var data: Data
    private lateinit var emergencyContactsCopy: ArrayList<EmergencyContact>
    private lateinit var infoWithEmergencyContact: InfoWithEmergencyContact

    private val _showInfo = MutableLiveData<Resource<Data>>()
    val showInfo: LiveData<Resource<Data>> = _showInfo


    // 页面跳转时的 ID
    var showInfoId: String? = null


    // info 页面数据
    private val _infoFragmentTitle = MutableLiveData<String>()
    val infoFragmentTitle: LiveData<String> = _infoFragmentTitle

    fun changeInfoTitle(title: String) {
        _infoFragmentTitle.value = title
    }

    private val _infoState = MutableLiveData<InfoState>()
    val infoState: LiveData<InfoState> = _infoState

    fun changeInfoState(infoState: InfoState) {
        when (infoState) {
            InfoState.NEW -> {
                data = Data(Array(INPUT_ARRAY_SIZE) { "" }, arrayListOf(EmergencyContact()))
            }
            InfoState.SHOW -> {
                data = Data(Array(INPUT_ARRAY_SIZE) { "" }, arrayListOf())
            }
            InfoState.EDIT -> {
                data.emergencyNumber.add(EmergencyContact())
            }
        }
        _infoState.value = infoState
    }


    // 我的页面数据
    private val _abstractInfo = MutableLiveData<Resource<List<AbstractInfo>>>()
    val abstractInfo: LiveData<Resource<List<AbstractInfo>>> = _abstractInfo

    // 用来判断返回我的页面时是否需要刷新数据
    var fromSaveInfo = false

    // 第一次进入页面时需要从远程获取数据
    init {
        viewModelScope.launch {
            fetchAbstractInfo(true)
        }
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
        val inputInfo = data.inputInfo
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
            data.emergencyNumber.add(it)
        }
        emergencyContactsCopy.addAll(emergencyContacts)
        _showInfo.value =
            if (result.succeeded) {
                Resource.Success(data)
            } else {
                Resource.Error(result.message!!, data)
            }
    }

    suspend fun deleteInfoWithEmergencyContact() {
        infoRepository.deleteInfoWithEmergencyContact(infoWithEmergencyContact)

    }


    fun updateAbstractInfo(abstractInfo: AbstractInfo) {
        viewModelScope.launch {
            infoRepository.updateItemChosen(abstractInfo)
        }

    }


    suspend fun save() {
        // 判断 info 状态
        val saveFromId = _infoState.value != InfoState.NEW
        val infoId = if (saveFromId) showInfoId!! else ""
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
        val inputInfo = data.inputInfo
        val date = Date(simpleDateFormat.parse(inputInfo[InputHint.BIRTHDATE])!!.time)
        val weight =
            if (inputInfo[InputHint.WEIGHT] == "") 0 else inputInfo[InputHint.WEIGHT].toInt()
        // 如果是新创建的，则给到
        val chosen = if (saveFromId) infoWithEmergencyContact.info.chosen else false
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
        val saveList = data.emergencyNumber.filter { it.phone != "" }

        if (saveFromId) {
            emergencyContactsCopy.forEach { old ->
                if (!data.emergencyNumber.any { it.id == old.id }) {
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
}

class Data(
    val inputInfo: Array<String>,
    val emergencyNumber: ArrayList<EmergencyContact>
)