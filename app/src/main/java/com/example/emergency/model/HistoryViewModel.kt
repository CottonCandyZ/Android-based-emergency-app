package com.example.emergency.model

import androidx.lifecycle.*
import com.example.emergency.data.local.repository.HistoryRepository
import com.example.emergency.util.getErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _state = MutableLiveData<STATE.History>()
    val state: LiveData<STATE.History> = _state
    lateinit var errorMessage: String

    val historyList = historyRepository.getHistory().asLiveData()


    fun refreshHistory() {
        viewModelScope.launch {
            try {
                historyRepository.refreshHistory()
                _state.value = STATE.History.REFRESH_COMPLETE
            } catch (e: Exception) {
                errorMessage = getErrorMessage(e)
                _state.value = STATE.History.REFRESH_ERROR
            }

        }

    }


}