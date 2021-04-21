package com.example.emergency.model

import androidx.lifecycle.*
import cn.leancloud.AVUser
import com.example.emergency.data.entity.AbstractInfo
import com.example.emergency.data.entity.Info
import com.example.emergency.data.entity.User
import com.example.emergency.data.local.repository.InfoRepository
import com.example.emergency.data.local.repository.UserRepository
import com.example.emergency.util.LogOut
import com.example.emergency.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyPageViewModel @Inject constructor(
    userRepository: UserRepository,
    private val infoRepository: InfoRepository,
    logOut: LogOut
) : ViewModel() {
    val abstractInfoList = infoRepository.getAbstractInfo().asLiveData()

    private val _status = MutableLiveData<STATUS.MyPage>()
    val status: LiveData<STATUS.MyPage> = _status
    lateinit var errorMessage: String
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _currentChosen = MutableLiveData<String>()
    val currentChosen: LiveData<String> = _currentChosen

    private lateinit var lastChosen: Info


    init {
        viewModelScope.launch {
            userRepository.getUser().collect {
                // 如果为空 说明用户未注册成功
                if (it.isEmpty()) {
                    logOut.clean()
                    AVUser.logOut()
                    _status.value = STATUS.MyPage.USER_NOT_FOUND
                } else {
                    _user.value = it[0]
                }
            }
        }
        viewModelScope.launch {
            infoRepository.getCurrentChosen().collect {
                if (it.isEmpty()) {
                    _currentChosen.value = "尚未选择"
                } else {
                    lastChosen = it[0]
                    _currentChosen.value = it[0].realName
                }
            }
        }
    }


    fun updateChosen(chosen: AbstractInfo) {
        viewModelScope.launch {
            try {
                infoRepository.updateItemChosen(lastChosen.id, chosen.id)
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                _status.value = STATUS.MyPage.CHOSEN_ERROR
            }

        }
    }


    fun refreshInfo() {
        viewModelScope.launch {
            try {
                infoRepository.refreshInfo()
                _status.value = STATUS.MyPage.REFRESH_COMPLETE
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                _status.value = STATUS.MyPage.REFRESH_ERROR
            }
        }
    }


}