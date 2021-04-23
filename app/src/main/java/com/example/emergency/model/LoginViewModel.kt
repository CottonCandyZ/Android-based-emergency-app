package com.example.emergency.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergency.data.local.repository.LoginRepository
import com.example.emergency.data.local.repository.UserRepository
import com.example.emergency.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _status = MutableLiveData<STATE.Login>()
    val state: LiveData<STATE.Login> = _status
    lateinit var errorMessage: String


    fun login(phone: String, pwd: String) {
        viewModelScope.launch {
            try {
                loginRepository.makeLoginRequest("+86$phone", pwd)
                userRepository.refresh()
                _status.value = STATE.Login.SUCCESS
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                _status.value = STATE.Login.ERROR
            }
        }
    }
}