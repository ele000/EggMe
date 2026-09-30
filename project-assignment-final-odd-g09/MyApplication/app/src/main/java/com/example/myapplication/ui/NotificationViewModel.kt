package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Notification
import com.example.myapplication.data.auth.SessionManagerFacade
import com.example.myapplication.domain.DeleteReadNotificationUseCase
import com.example.myapplication.domain.GetNotificationsByUserUseCase
import com.example.myapplication.domain.UpdateNotificationReadUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val getNotificationsByUserUseCase: GetNotificationsByUserUseCase,
    private val updateNotificationReadUseCase: UpdateNotificationReadUseCase,
    private val deleteReadNotificationUseCase: DeleteReadNotificationUseCase
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _lastNotificationForSnackbar = MutableSharedFlow<Notification>(extraBufferCapacity = 1)
    val lastNotificationForSnackbar = _lastNotificationForSnackbar.asSharedFlow()

    private var notificationJob: Job? = null
    private var initJob: Job? = null

    init {
        initJob = viewModelScope.launch {
            SessionManagerFacade.currentUserStateFlow.collect { userId ->
                loadNotifications(userId)
            }
        }
    }

    private fun loadNotifications(userId: String?){

        notificationJob?.cancel()

        notificationJob = viewModelScope.launch{

            var previousNotificationsIds = emptySet<String>()
            var firstLoad = true

            getNotificationsByUserUseCase(userId).collect { notificationsList ->
                _notifications.update { notificationsList.sortedByDescending { it.createdAt } }

                if(firstLoad) {
                    previousNotificationsIds = notificationsList.map { it.id }.toSet()
                    firstLoad = false
                } else {
                    val currentNotificationsIds = notificationsList.map { it.id }.toSet()

                    val newNotifications = notificationsList.filter { it.id !in previousNotificationsIds }

                    previousNotificationsIds = currentNotificationsIds

                    newNotifications.forEach {
                        _lastNotificationForSnackbar.tryEmit(it)
                    }
                }

            }
        }
    }

    fun onNotificationClicked(notification: Notification) {
        _notifications.update { list ->
            list.map { if (it.id == notification.id) it.copy(isRead = true) else it }
        }

        viewModelScope.launch {
            try {
                updateNotificationReadUseCase(notification)
            } catch (e: Exception) {
                _notifications.update { list ->
                    list.map { if (it.id == notification.id) it.copy(isRead = false) else it }
                }
            }
        }
    }

    fun onDeleteReadNotification(notification: Notification){
        viewModelScope.launch {
            try {
                deleteReadNotificationUseCase(notification)
                _notifications.update {
                        notificationList -> notificationList.filter { it.id != notification.id }
                }
            } catch (e: Exception) {
                ///
            }
        }
    }

    companion object {
        fun provideFactory(
            getNotificationsByUserUseCase: GetNotificationsByUserUseCase,
            updateNotificationReadUseCase: UpdateNotificationReadUseCase,
            deleteReadNotificationUseCase: DeleteReadNotificationUseCase
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NotificationViewModel(
                        getNotificationsByUserUseCase,
                        updateNotificationReadUseCase,
                        deleteReadNotificationUseCase
                    ) as T
                }
            }
    }
}