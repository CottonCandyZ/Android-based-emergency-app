package com.example.emergency.data.local.repository

import com.example.emergency.data.entity.Call
import com.example.emergency.data.remote.EmergencyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class EmergencyRepository @Inject constructor(
    private val emergencyService: EmergencyService
) {
    suspend fun submitOneCall(call: Call) {
        withContext(Dispatchers.IO) {
            emergencyService.submitOneCall(call)
        }
    }

}