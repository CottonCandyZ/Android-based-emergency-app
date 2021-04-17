package com.example.emergency.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergency.data.local.repository.LoginRepository
import com.example.emergency.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _status = MutableLiveData<STATUS.Login>()
    val status: LiveData<STATUS.Login> = _status
    lateinit var errorMessage: String


    fun login(phone: String, pwd: String) {
        viewModelScope.launch {
            try {
                loginRepository.makeLoginRequest("+86$phone", pwd)
                _status.value = STATUS.Login.SUCCESS
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                _status.value = STATUS.Login.ERROR
            }
        }
    }
}