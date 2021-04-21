package com.example.emergency.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergency.data.entity.EmergencyContact
import com.example.emergency.data.entity.Info
import com.example.emergency.data.entity.InfoWithEmergencyContact
import com.example.emergency.data.local.repository.InfoRepository
import com.example.emergency.ui.info.InputHint
import com.example.emergency.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

const val INPUT_ARRAY_SIZE = 11

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {

    private val _showInfo = MutableLiveData<InputData>()
    val showInfo: LiveData<InputData> = _showInfo
    private val _status = MutableLiveData<STATUS.Info>()
    val status: LiveData<STATUS.Info> = _status
    lateinit var errorMessage: String

    // 记录更改的差异性
    private lateinit var emergencyContactsCopy: ArrayList<EmergencyContact>


    // 标题
    private val _infoFragmentTitle = MutableLiveData<String>()
    val infoFragmentTitle: LiveData<String> = _infoFragmentTitle


    lateinit var inputData: InputData
    private lateinit var infoWithEmergencyContact: InfoWithEmergencyContact

    fun setInfoFragmentTitle(title: String) {
        _infoFragmentTitle.value = title
    }


    fun setStatus(status: STATUS.Info) {
        when (status) {
            STATUS.Info.NEW -> {
                inputData =
                    InputData(Array(INPUT_ARRAY_SIZE) { "" }, arrayListOf(EmergencyContact()))
            }
            STATUS.Info.SHOW -> {
                inputData = InputData(Array(INPUT_ARRAY_SIZE) { "" }, arrayListOf())
            }
            STATUS.Info.EDIT -> {
                inputData.emergencyNumber.add(EmergencyContact())
            }
            else -> {
            }
        }
        _status.value = status
    }

    fun getStatus(): STATUS.Info {
        return status.value!!
    }


    // 从本地数据库取数据
    fun fetchInfo(infoId: String) {
        viewModelScope.launch {
            emergencyContactsCopy = arrayListOf()
            val result = infoRepository.getInfoWithEmergencyContact(infoId)
            infoWithEmergencyContact = result[0]
            val info = infoWithEmergencyContact.info
            val inputInfo = inputData.inputInfo
            info.run {
                with(InputHint) {
                    inputInfo[REAL_NAME] = realName
                    inputInfo[SEX] = sex ?: ""
                    inputInfo[BIRTHDATE] =
                        SimpleDateFormat("yyyy/MM/dd", Locale.CHINA).format(birthdate)
                    inputInfo[PHONE] = phone
                    inputInfo[WEIGHT] = weight.toString()
                    inputInfo[BLOOD_TYPE] = bloodType ?: ""
                    inputInfo[MEDICAL_CONDITIONS] = medicalConditions ?: ""
                    inputInfo[MEDICAL_NOTES] = medicalConditions ?: ""
                    inputInfo[ALLERGY] = allergy ?: ""
                    inputInfo[MEDICATIONS] = medications ?: ""
                    inputInfo[ADDRESS] = address ?: ""
                }
            }
            val emergencyContacts = infoWithEmergencyContact.emergencyContacts
            emergencyContacts.forEach {
                inputData.emergencyNumber.add(it)
            }
            emergencyContactsCopy.addAll(emergencyContacts)
            _showInfo.value = inputData
        }
    }


    fun deleteInfoWithEmergencyContact() {
        viewModelScope.launch {
            val notChosen = infoRepository.getNotChosen()
            if (infoWithEmergencyContact.info.chosen) {
                if (notChosen.isNotEmpty()) {
                    infoRepository.updateInfoChosenWithOutRemove(notChosen[0])
                }
            }
            try {
                infoRepository.deleteInfoWithEmergencyContact(infoWithEmergencyContact)
                _status.value = STATUS.Info.DELETE_SUCCESS
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                _status.value = STATUS.Info.DELETE_ERROR
            }


        }
    }

    fun save() {
        viewModelScope.launch {
            try {
                // 判断 info 状态
                val saveFromId = _status.value != STATUS.Info.NEW
                val infoId = if (saveFromId) infoWithEmergencyContact.info.id else ""
                val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)

                val inputInfo = inputData.inputInfo

                val date = Date(simpleDateFormat.parse(inputInfo[InputHint.BIRTHDATE])!!.time)
                val weight =
                    if (inputInfo[InputHint.WEIGHT] == "") 0 else inputInfo[InputHint.WEIGHT].toInt()

                // 判断当前数据库是否还有数据
                val listNumber = infoRepository.getInfoNumber()
                // 如果是新创建的，则给到


                val chosen =
                    if (saveFromId) infoWithEmergencyContact.info.chosen else listNumber == 0
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
                setStatus(STATUS.Info.SAVE_SUCCESS)
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                setStatus(STATUS.Info.SAVE_ERROR)
            }
        }

    }


    class InputData(
        val inputInfo: Array<String>,
        val emergencyNumber: ArrayList<EmergencyContact>
    )
}