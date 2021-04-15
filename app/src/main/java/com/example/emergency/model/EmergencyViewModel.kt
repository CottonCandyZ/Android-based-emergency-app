package com.example.emergency.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergency.data.entity.Info
import com.example.emergency.data.local.InfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class STATUS {
    INIT, CALLING, CANCEL
}

@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {
    private val _status = MutableLiveData(STATUS.INIT)
    val status: LiveData<STATUS> = _status

    private val _currentText = MutableLiveData<String>()
    val currentText: LiveData<String> = _currentText
    var info: Info? = null


    fun refresh() {
        viewModelScope.launch {
            info = infoRepository.getEmergencyChosen()
            if (info == null) {
                _currentText.value = "请添加呼救人"
            } else {
                _currentText.value = "点击以为${info!!.realName}呼救"
            }
        }
    }

    fun setState(status: STATUS) {
        when (status) {
            STATUS.CALLING -> {
                _currentText.value = "正在为${info!!.realName}呼救"
            }
            STATUS.CANCEL -> {
                _currentText.value = "已取消，再次点击以呼救"
            }
        }
        _status.value = status

    }
}