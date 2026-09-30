package com.example.myapplication.domain

import com.example.myapplication.data.Notification

class UpdateNotificationReadUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notification: Notification){
        notificationRepository.updateNotification(
            notification.copy(isRead = true)
        )
    }
}