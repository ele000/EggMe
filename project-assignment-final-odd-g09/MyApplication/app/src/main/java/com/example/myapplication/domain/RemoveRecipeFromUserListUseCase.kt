package com.example.myapplication.domain

import com.example.myapplication.domain.UserRepository
import com.example.myapplication.domain.RecipeRepository

class RemoveRecipeFromUserListUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: String, title: String, recipeId: String
    ): Boolean {
        return userRepository.removeRecipeFromUserList(userId, title, recipeId)
    }
}