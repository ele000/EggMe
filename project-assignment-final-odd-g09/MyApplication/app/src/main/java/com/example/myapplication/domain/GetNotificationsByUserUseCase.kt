package com.example.myapplication.domain

import com.example.myapplication.data.FirebaseNotificationRepository
import com.example.myapplication.data.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetNotificationsByUserUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(userId: String?): Flow<List<Notification>> {

        if (userId == null) {
            return flowOf(emptyList())
        }
        return notificationRepository.getNotificationsByUser(userId)
    }
}