package com.example.emergency.ui.info

import androidx.lifecycle.ViewModel

import androidx.lifecycle.ViewModelProvider
import com.example.emergency.data.InfoRepository


class InfoViewModelFactory(private val infoRepository: InfoRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return InfoViewModel(infoRepository) as T
    }
}