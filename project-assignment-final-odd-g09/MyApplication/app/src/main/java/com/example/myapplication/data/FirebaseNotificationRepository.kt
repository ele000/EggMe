package com.example.myapplication.data

import android.content.Context
import com.example.myapplication.domain.Collections
import com.example.myapplication.domain.NotificationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseNotificationRepository(
    private val firestore: FirebaseFirestore
) : NotificationRepository {

    private val notificationsCollection = firestore.collection(Collections.NOTIFICATIONS)

    suspend fun initializeData(context: Context) {
        try {
            val existing = notificationsCollection.limit(1).get().await()
            if (existing.isEmpty) {
                val jsonString =
                    context.assets.open("notifications_seed.json").bufferedReader()
                        .use { it.readText() }
                val placeholderNotifications =
                    Gson().fromJson(jsonString, Array<Notification>::class.java).toList()

                firestore.runBatch { batch ->
                    placeholderNotifications.forEach { notification ->
                        val docRef = notificationsCollection.document(notification.id)
                        batch.set(docRef, notification)
                    }
                }.await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getNotificationsByUser(userId: String): Flow<List<Notification>> {
        return notificationsCollection
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Notification::class.java)
            }
    }

    override suspend fun updateNotification(notification: Notification) {
        try {
            notificationsCollection.document(notification.id)
                .set(notification)
                .await()
        } catch (e: Exception) {
            throw e
        }

    }

    override suspend fun deleteNotification(notification: Notification){
        try {
            notificationsCollection.document(notification.id)
                .delete()
                .await()
        } catch (e: Exception) {
            throw e
        }
    }
}