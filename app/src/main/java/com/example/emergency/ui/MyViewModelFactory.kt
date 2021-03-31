package com.example.emergency.ui

import androidx.lifecycle.ViewModel

import androidx.lifecycle.ViewModelProvider
import com.example.emergency.data.InfoRepository


class MyViewModelFactory(private val infoRepository: InfoRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MyViewModel(infoRepository) as T
    }
}