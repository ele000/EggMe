package com.example.myapplication.domain

import com.example.myapplication.data.Notification

class DeleteReadNotificationUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        notification: Notification
    ){
        if(notification.isRead) {
            return notificationRepository.deleteNotification(notification)
        }
    }
}