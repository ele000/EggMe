package com.example.myapplication.data.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.data.auth.SessionManagerFacade
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.google.firebase.messaging.FirebaseMessagingService
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner


//Firebase Cloud Messaging:  https://www.youtube.com/watch?v=q6TL2RyysV4&t
//Official documentation:  https://firebase.google.com/docs/cloud-messaging?hl=it
//Lifecycle https://developer.android.com/jetpack/androidx/releases/lifecycle?hl=it

class PushNotificationService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val userId = SessionManagerFacade.currentUserStateFlow.value ?: return
        val userRepository = (application as MyApplication).container.userRepository

        CoroutineScope(Dispatchers.IO).launch {
            try {
                userRepository.addFcmToken(userId, token)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM_TEST", "Messaggio ricevuto: ${remoteMessage.data}")

        val title = remoteMessage.data["title"] ?: "Sono un titolo di dafault"
        val body = remoteMessage.data["body"] ?: "Sono un messaggio di default"
        val recipeId = remoteMessage.data["recipeId"] ?: return

        val currentLifecycleState = ProcessLifecycleOwner.get().lifecycle.currentState

        if (currentLifecycleState.isAtLeast(Lifecycle.State.STARTED)) {
            //the app is visible, the snackbar will appear like in lab5
        } else {
            val deepLinkUri = "https://eggme.com/recipe/$recipeId".toUri()

            val intent = Intent(Intent.ACTION_VIEW, deepLinkUri, this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

            //when user click on the notifciation, send the intent to open the app in a recipe screen (defined by the link)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                this,
                recipeId.hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notification = NotificationCompat.Builder(this, "recipe_notifications")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setColor(getColor(R.color.myyellow))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(recipeId.hashCode(), notification)
        }

    }
}

