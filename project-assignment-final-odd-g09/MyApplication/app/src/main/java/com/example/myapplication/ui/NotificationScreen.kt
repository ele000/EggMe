package com.example.myapplication.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Author
import com.example.myapplication.data.Notification
import com.example.myapplication.data.NotificationType
import com.example.myapplication.ui.commoncomponents.ProfilePicture
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.Timestamp


@Composable
fun PresentationPaneNotifications(
    notifications: List<Notification> ,
    onNotificationClick: (String) -> Unit ,
    onNotificationUpdate: (Notification) -> Unit ,
    onDeleteReadNotification: (Notification) -> Unit
) {

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        item {
            notifications.forEach {
                notification ->
                NotificationView(notification,onNotificationClick,onNotificationUpdate,onDeleteReadNotification)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    onNotificationClick: (String) -> Unit,
    onBack: () -> Unit,
) {

    val notifications by viewModel.notifications.collectAsState()

    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("Notifications") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0),
            navigationIcon = {
                IconButton(
                    onClick = {
                        onBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            },
            actions = {
                //
            }
        )
         PresentationPaneNotifications(
                                        notifications ,
                                        onNotificationClick,
                                        viewModel::onNotificationClicked,
                                        viewModel::onDeleteReadNotification)
    }
}

@Composable
fun NotificationView(
    notification: Notification,
    onNotificationClick: (String) -> Unit,
    onNotificationUpdate: (Notification) -> Unit,
    onDeleteReadNotification: (Notification) -> Unit
){

    Column(
        Modifier.fillMaxWidth(0.9f)
    ) {
        FilledTonalButton(
            onClick = {
                onNotificationUpdate(notification)
                onNotificationClick(notification.recipeId) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors =
                ButtonDefaults.filledTonalButtonColors(
                    containerColor = if( notification.isRead)  MaterialTheme.colorScheme.surfaceVariant
                                     else MaterialTheme.colorScheme.primaryContainer
                )
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ){
                ProfilePicture(notification.author,50.dp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(3f),
            ){

                Spacer(modifier = Modifier.height(4.dp))

                Row() {
                    Text(
                        text = notification.title,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row() {
                    Text(
                        text = if(notification.author.id != "") notification.author.name+ " "+notification.author.surname+" "+notification.message
                            else notification.message
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row() {
                    Text(
                        text = formatRelativeTime(notification.createdAt)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
            }

            if(notification.isRead) {
                Column(modifier = Modifier.weight(0.5f)) {
                    IconButton(
                        onClick = { onDeleteReadNotification(notification) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "delete notification",
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }

}


private fun formatRelativeTime(timestamp: Timestamp?): String {
    val time = timestamp ?: return ""
    val diffMillis = System.currentTimeMillis() - time.toDate().time
    if (diffMillis < 60_000L) return "a few seconds ago"

    val minutes = diffMillis / 60_000L
    if (minutes < 60L) return if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"

    val hours = minutes / 60L
    if (hours < 24L) return if (hours == 1L) "1 hour ago" else "$hours hours ago"

    val days = hours / 24L
    return if (days == 1L) "1 day ago" else "$days days ago"
}