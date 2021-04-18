package com.example.emergency.data.local.repository

import com.example.emergency.data.entity.History
import com.example.emergency.data.local.dao.HistoryDao
import com.example.emergency.data.remote.HistoryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao,
    private val historyService: HistoryService
) {
    fun getHistory(): Flow<List<History>> {
        return historyDao.getHistory()
    }

    suspend fun refreshHistory() {
        withContext(Dispatchers.IO) {
            val remote = historyService.getHistory()
            historyDao.nukeTable()
            historyDao.insertHistory(*remote.toTypedArray())
        }
    }
}