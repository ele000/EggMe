package com.example.myapplication.domain

import com.example.myapplication.domain.UserRepository
import com.example.myapplication.domain.RecipeRepository
import com.example.myapplication.data.Recipe
import kotlinx.coroutines.flow.first

class ToggleFavoriteRecipeUseCase (
    private val userRepository: UserRepository
){
    suspend operator fun invoke(userId: String, recipe: Recipe
    ): Boolean {
        val user = userRepository.getUser(userId).first() ?: return false
        val isFavorite = user.userLists["Favorite"]?.any { it.id == recipe.id } == true

        return if (isFavorite) {
            userRepository.removeRecipeFromUserList(userId, "Favorite", recipe.id)
        } else {
            userRepository.addRecipeToUserList(userId, "Favorite", recipe)
        }
    }
}