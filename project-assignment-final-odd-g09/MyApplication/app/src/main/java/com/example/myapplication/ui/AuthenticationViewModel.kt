package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.User
import com.example.myapplication.data.auth.SessionManagerFacade
import com.example.myapplication.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun registerUser(result: Result<Unit>,onNewUser: () -> Unit,onSuccess: () -> Unit){
        if (result.isFailure) return

        val authUser = SessionManagerFacade.getCurrentUser() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = authUser.uid
                val newUser = User(id = userId, email = authUser.email.orEmpty(), userName = authUser.displayName.orEmpty())

                val alreadyPresent = userRepository.registerUserIfNotPresent(newUser)

                if (alreadyPresent) {
                    onSuccess()
                } else {
                    onNewUser()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }

    }

    companion object {
        fun provideFactory(
            userRepository: UserRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthenticationViewModel(userRepository) as T
            }
        }
    }
}