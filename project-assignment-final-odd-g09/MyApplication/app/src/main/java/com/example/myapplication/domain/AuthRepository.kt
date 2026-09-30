package com.example.myapplication.domain

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUserId: String?
    val currentUserState: Flow<String?>
    val currentUserStateFlow: StateFlow<String?>
    suspend fun signIn(context: Context): Result<Unit>
    suspend fun signInAnonymous(context: Context): Result<Unit>
    val isLoggedIn: Boolean
    suspend fun logOut()
}