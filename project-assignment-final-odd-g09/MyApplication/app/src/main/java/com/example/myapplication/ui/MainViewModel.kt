package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Author
import com.example.myapplication.data.auth.SessionManagerFacade
import com.example.myapplication.domain.GetAuthUserDataUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
class MainViewModel(private val getAuthUserDataUseCase: GetAuthUserDataUseCase) : ViewModel() {
    private val _authUser = MutableStateFlow<Author?>(null)
    val authUser: StateFlow<Author?> = _authUser.asStateFlow()
    private var job: Job? = null
    private var userJob: Job? = null

    init {
        loadAuthenticatedUser()
    }

    private fun loadAuthenticatedUser() {
        job = viewModelScope.launch {
            SessionManagerFacade.currentUserStateFlow.collect { userId ->

                userJob?.cancel()

                userJob = viewModelScope.launch {
                    getAuthUserDataUseCase(userId).collect { user ->
                        _authUser.update { user }
                    }
                }
            }
        }
    }

    companion object {
        fun provideFactory(getAuthUserDataUseCase: GetAuthUserDataUseCase): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(getAuthUserDataUseCase) as T
                }
            }
    }
}