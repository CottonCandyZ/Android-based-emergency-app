package com.example.emergency.ui.info

import androidx.lifecycle.ViewModel
import com.example.emergency.data.InfoRepository
import com.example.emergency.model.EmergencyContact
import com.example.emergency.model.Info
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*


const val INPUT_ARRAY_SIZE = 11

class InfoViewModel(
    private val infoRepository: InfoRepository
) : ViewModel() {
    // 用于保存填入的信息
    val inputInfo: Array<String> = Array(INPUT_ARRAY_SIZE) { "" }
    val emergencyNumber: ArrayList<Array<String>> = arrayListOf(arrayOf("", ""))
    lateinit var info: Info
    suspend fun save() {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
        val date = Date(simpleDateFormat.parse(inputInfo[InputHint.BIRTHDATE])!!.time)
        info = Info(
            1,
            inputInfo[InputHint.ADDRESS],
            inputInfo[InputHint.SEX],
            date,
            inputInfo[InputHint.PHONE],
            inputInfo[InputHint.WEIGHT].toInt(),
            inputInfo[InputHint.BLOOD_TYPE],
            inputInfo[InputHint.MEDICAL_CONDITIONS],
            inputInfo[InputHint.MEDICAL_NOTES],
            inputInfo[InputHint.ALLERGY],
            inputInfo[InputHint.MEDICATIONS],
            inputInfo[InputHint.ADDRESS],
        )
        val id = infoRepository.saveInfo(info)
        for (strings in emergencyNumber) {
            infoRepository.saveEmergencyContact(EmergencyContact(id, strings[0], strings[1]))
        }
    }

}