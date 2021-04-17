package com.example.emergency.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergency.data.local.repository.SignUpRepository
import com.example.emergency.data.local.repository.UserRepository
import com.example.emergency.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpRepository: SignUpRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _status = MutableLiveData(STATUS.SignUp.INIT)
    val status: LiveData<STATUS.SignUp> = _status
    lateinit var errorMessage: String

    fun getStatus(): STATUS.SignUp {
        return status.value!!
    }

    fun setStatus(status: STATUS.SignUp) {
        _status.value = status
    }

    fun signUp(phone: String, code: String) {
        viewModelScope.launch {
            try {
                if (signUpRepository.judgeUserIfExist(phone)) {
                    signUpRepository.checkCodeToSignUpOrLogin(phone, code)
                    // 刷新当前数据库存储的用户信息
                    userRepository.refresh()
                    setStatus(STATUS.SignUp.OLD_UER_LOGIN)
                } else {
                    signUpRepository.checkCodeToSignUpOrLogin(phone, code)
                    setStatus(STATUS.SignUp.NEW_USER)
                }
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                setStatus(STATUS.SignUp.SIGN_UP_ERROR)
            }

        }
    }

    fun saveUser(phone: String, userName: String, pwd: String) {
        viewModelScope.launch {
            try {
                signUpRepository.saveUser(phone, userName, pwd)
                // 刷新当前数据库存储的用户信息
                userRepository.refresh()
                setStatus(STATUS.SignUp.SAVE_USER_SUCCESS)
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                setStatus(STATUS.SignUp.SIGN_UP_ERROR)
            }
        }
    }

    fun sendCodeForSignUp(phone: String) {
        viewModelScope.launch {
            try {
                signUpRepository.sendCodeForSignUp(phone)
                setStatus(STATUS.SignUp.SEND_CODE_SUCCESS)
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                setStatus(STATUS.SignUp.ERROR)
            }
        }
    }
}