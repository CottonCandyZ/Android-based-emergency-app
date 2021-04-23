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
    private val _state = MutableLiveData(STATE.SignUp.INIT)
    val state: LiveData<STATE.SignUp> = _state
    lateinit var errorMessage: String

    fun getStatus(): STATE.SignUp {
        return state.value!!
    }

    fun setStatus(state: STATE.SignUp) {
        _state.value = state
    }

    fun signUp(phone: String, code: String) {
        viewModelScope.launch {
            try {
                if (signUpRepository.judgeUserIfExist(phone)) {
                    signUpRepository.checkCodeToSignUpOrLogin(phone, code)
                    // 刷新当前数据库存储的用户信息
                    userRepository.refresh()
                    setStatus(STATE.SignUp.OLD_UER_LOGIN)
                } else {
                    signUpRepository.checkCodeToSignUpOrLogin(phone, code)
                    setStatus(STATE.SignUp.NEW_USER)
                }
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                setStatus(STATE.SignUp.SIGN_UP_ERROR)
            }

        }
    }

    fun saveUser(phone: String, userName: String, pwd: String) {
        viewModelScope.launch {
            try {
                signUpRepository.saveUser(phone, userName, pwd)
                // 刷新当前数据库存储的用户信息
                userRepository.refresh()
                setStatus(STATE.SignUp.SAVE_USER_SUCCESS)
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                setStatus(STATE.SignUp.SIGN_UP_ERROR)
            }
        }
    }

    fun sendCodeForSignUp(phone: String) {
        viewModelScope.launch {
            try {
                signUpRepository.sendCodeForSignUp(phone)
                setStatus(STATE.SignUp.SEND_CODE_SUCCESS)
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                setStatus(STATE.SignUp.ERROR)
            }
        }
    }
}