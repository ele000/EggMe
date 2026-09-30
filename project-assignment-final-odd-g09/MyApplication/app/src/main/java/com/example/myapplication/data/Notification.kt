package com.example.myapplication.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import okhttp3.internal.notify

data class Notification(
    val id: String = "",
    val userId: String = "",
    val type: NotificationType = NotificationType.IMPORTED,
    val title: String = "",
    val message: String = "",
    val recipeId: String = "",
    val author: Author = Author(),
    val createdAt: Timestamp = Timestamp(0,0),
    @get:PropertyName("isRead")
    val isRead: Boolean = false
)

enum class NotificationType{
    IMPORTED,
    REVIEW,
    RECOMMENDED
}