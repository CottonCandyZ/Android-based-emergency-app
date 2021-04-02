package com.example.emergency.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergency.R
import com.example.emergency.data.InfoRepository
import com.example.emergency.model.AbstractInfo
import com.example.emergency.model.EmergencyContact
import com.example.emergency.model.Info
import com.example.emergency.model.InfoWithEmergencyContact
import com.example.emergency.ui.info.InputHint
import com.example.emergency.util.showError
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*


const val INPUT_ARRAY_SIZE = 11
enum class InfoState {
    SHOW, NEW
}

class MyViewModel(
    private val infoRepository: InfoRepository,
    context: Context
) : ViewModel() {
    // 用于保存填入的信息
    lateinit var inputInfo: Array<String>
    lateinit var emergencyNumber: ArrayList<Array<String>>


    // hints
    val inputHints = listOf(
        context.getString(R.string.info_add_real_name_hint),
        context.getString(R.string.info_add_sex_hint),
//            context.getString(R.string.info_relationship),
        context.getString(R.string.info_add_birth_hint),
        context.getString(R.string.info_add_phone_hint),
        context.getString(R.string.info_add_weight_hint),
        context.getString(R.string.info_add_blood_type_hint),
        context.getString(R.string.info_add_medical_conditions_hint),
        context.getString(R.string.info_add_medical_notes_hint),
        context.getString(R.string.info_add_allergy_hint),
        context.getString(R.string.info_add_medications_hint),
        context.getString(R.string.info_add_address_hint)
    )

    // spinner selection
    val spinnerList = listOf(
        listOf("男", "女"),
        listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"),
        listOf(
            "家人",
            "母亲",
            "父亲",
            "父母",
            "兄弟",
            "姐妹",
            "儿子",
            "女儿",
            "子女",
            "朋友",
            "配偶",
            "伴侣",
            "助理",
            "上司",
            "医生",
            "紧急联系人",
            "家庭成员",
            "老师",
            "看护",
            "监护人",
            "社会工作者",
            "学校",
            "托儿所"
        )
    )

    // 页面跳转时的 ID
    var showInfoId: String? = null

    private lateinit var infoWithEmergencyContact: InfoWithEmergencyContact


    // info 页面数据
    private val _infoFragmentTitle = MutableLiveData<String>()
    val infoFragmentTitle: LiveData<String> = _infoFragmentTitle

    fun changeInfoTitle(title: String) {
        _infoFragmentTitle.value = title
    }

    var infoState = InfoState.NEW


    // 我的页面数据
    private val _abstractInfo = MutableLiveData<List<AbstractInfo>>()
    val abstractInfo: LiveData<List<AbstractInfo>> = _abstractInfo

    // 用来判断返回我的页面时是否需要刷新数据
    var fromSaveInfo = false

    // 第一次进入页面时需要从远程获取数据
    init {
        viewModelScope.launch {
            try {
                fetchAbstractInfo(true)
            } catch (e: Exception) {
                fetchAbstractInfo(false)
                showError(e.cause!!, context)
            }
        }
    }

    // 获取我的页面的数据
    suspend fun fetchAbstractInfo(remote: Boolean) {
        _abstractInfo.value = infoRepository.getAbstractInfo(remote)
    }

    // 获取详细信息的数据
    suspend fun fetchInfo(remote: Boolean) {

        val result = infoRepository.getInfo(showInfoId!!, remote)
        if (result.isEmpty()) {
        } else {
            infoWithEmergencyContact = result[0]
        }
        val info = infoWithEmergencyContact.info
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
        inputInfo = Array(INPUT_ARRAY_SIZE) { "" }
        emergencyNumber = arrayListOf()
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
            emergencyNumber.add(arrayOf(it.phone, it.relationship))
        }
    }


    fun cleanup() {
        inputInfo = Array(INPUT_ARRAY_SIZE) { "" }
        emergencyNumber = arrayListOf(arrayOf("", ""))
    }

    suspend fun save(saveFromId: Boolean) {
        var infoId = ""
        if (saveFromId) {
            infoId = showInfoId!!
        }
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
        val date = Date(simpleDateFormat.parse(inputInfo[InputHint.BIRTHDATE])!!.time)
        val weight =
            if (inputInfo[InputHint.WEIGHT] == "") 0 else inputInfo[InputHint.WEIGHT].toInt()
        val info = Info(
            infoId,
            inputInfo[InputHint.REAL_NAME],
            inputInfo[InputHint.SEX],
            date,
            inputInfo[InputHint.PHONE],
            weight,
            inputInfo[InputHint.BLOOD_TYPE],
            inputInfo[InputHint.MEDICAL_CONDITIONS],
            inputInfo[InputHint.MEDICAL_NOTES],
            inputInfo[InputHint.ALLERGY],
            inputInfo[InputHint.MEDICATIONS],
            inputInfo[InputHint.ADDRESS],
        )
        if (infoId == "") {
            infoId = infoRepository.saveInfo(info, false)
        } else {
            infoRepository.saveInfo(info, true)
        }

        val saveList = emergencyNumber.filter { it[0] != "" }
        for (strings in saveList) {
            infoRepository.saveEmergencyContact(
                EmergencyContact(
                    infoId = infoId,
                    phone = strings[0],
                    relationship = strings[1],
                )
            )
        }
    }
}