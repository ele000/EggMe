package com.example.myapplication.domain

import com.example.myapplication.data.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun getNotificationsByUser(userId: String): Flow<List<Notification>>

    suspend fun updateNotification(notification: Notification)

    suspend fun deleteNotification(notification: Notification)
}