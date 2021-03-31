package com.example.emergency.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergency.data.InfoRepository
import com.example.emergency.model.AbstractInfo
import com.example.emergency.model.EmergencyContact
import com.example.emergency.model.Info
import com.example.emergency.ui.info.InputHint
import com.example.emergency.util.showError
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*


const val INPUT_ARRAY_SIZE = 11

class MyViewModel(
    private val infoRepository: InfoRepository,
    context: Context
) : ViewModel() {
    // 用于保存填入的信息
    lateinit var inputInfo: Array<String>
    lateinit var emergencyNumber: ArrayList<Array<String>>
    private val _abstractInfo = MutableLiveData<List<AbstractInfo>>()
    val abstractInfo: LiveData<List<AbstractInfo>> = _abstractInfo
    var fromSaveInfo = false

    init {
        viewModelScope.launch {
            try {
                fetch(true)
            } catch (e: Exception) {
                fetch(false)
                showError(e.cause!!, context)
            }

        }

    }


    lateinit var info: Info
    suspend fun fetch(remote: Boolean) {
        _abstractInfo.value = infoRepository.getAbstractInfo(remote)
    }

    fun cleanup() {
        inputInfo = Array(INPUT_ARRAY_SIZE) { "" }
        emergencyNumber = arrayListOf(arrayOf("", ""))
    }

    suspend fun save() {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
        val date = Date(simpleDateFormat.parse(inputInfo[InputHint.BIRTHDATE])!!.time)
        val weight =
            if (inputInfo[InputHint.WEIGHT] == "") 0 else inputInfo[InputHint.WEIGHT].toInt()
        info = Info(
            "",
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
        val id = infoRepository.saveInfo(info)
        val saveList = emergencyNumber.filter { it[0] != "" }
        for (strings in saveList) {
            infoRepository.saveEmergencyContact(EmergencyContact(id, strings[0], strings[1]))
        }
    }

}