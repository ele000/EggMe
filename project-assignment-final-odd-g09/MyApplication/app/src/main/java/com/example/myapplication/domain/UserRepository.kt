package com.example.myapplication.domain

import com.example.myapplication.data.Recipe
import com.example.myapplication.data.RecipeForCard
import com.example.myapplication.data.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(userId: String): Flow<User?>
    fun getUserLists(userId: String): Flow<Map<String, List<RecipeForCard>>?>
    suspend fun updateUser(user: User)
    suspend fun addList(userId: String, title: String): Boolean
    suspend fun removeList(userId: String, title: String): Boolean
    suspend fun addRecipeToUserList(userId: String, title: String, recipe: Recipe): Boolean
    suspend fun removeRecipeFromUserList(userId: String, title: String, recipeId: String): Boolean
    suspend fun registerUserIfNotPresent(newUser: User): Boolean
    suspend fun addFcmToken(userId: String, token: String)
    suspend fun removeFcmToken(userId: String, token: String)

}

