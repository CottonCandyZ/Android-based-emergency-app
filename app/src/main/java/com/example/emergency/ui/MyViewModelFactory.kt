package com.example.emergency.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.emergency.WebService
import com.example.emergency.data.AppDatabase
import com.example.emergency.data.InfoRepository


class MyViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    private val database = AppDatabase.getInstance(context)
    private val infoRepository =
        InfoRepository(database.infoDao(), database.emergencyContactDao(), WebService.getInstance())

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MyViewModel(infoRepository, context) as T
    }
}