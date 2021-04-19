package com.example.emergency.data.local.repository

import com.example.emergency.data.entity.Call
import com.example.emergency.data.entity.History
import com.example.emergency.data.entity.Location
import com.example.emergency.data.local.dao.HistoryDao
import com.example.emergency.data.remote.EmergencyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class EmergencyRepository @Inject constructor(
    private val emergencyService: EmergencyService,
    private val historyDao: HistoryDao,
) {
    suspend fun submitOneCall(call: Call): String =
        withContext(Dispatchers.IO) {
            return@withContext emergencyService.submitOneCall(call)
        }


    suspend fun submitPosition(callId: String, location: Location) {
        withContext(Dispatchers.IO) {
            emergencyService.submitLocation(callId, location)
        }
    }

    fun getStatus(callId: String): Flow<List<History>> {
        return historyDao.getStatus(callId)

    }

    suspend fun setStatus(callId: String, status: String) {
        withContext(Dispatchers.IO) {
            emergencyService.setStatus(callId, status)
        }

    }
}