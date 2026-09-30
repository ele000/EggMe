package com.example.myapplication.domain

import com.example.myapplication.domain.UserRepository
import com.example.myapplication.domain.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetUserRecipesUseCase(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) {
    operator fun invoke(userId: String): Flow<List<RecipeWithSaved>> {
        return combine(
            recipeRepository.getRecipes(),
            userRepository.getUserLists(userId)
        ) { recipes, userLists ->
            val favoriteRecipes = userLists?.get("Favorite") ?: emptyList()
            recipes.filter { recipe ->
                recipe.author.id == userId
            }.map { recipe ->
                RecipeWithSaved(
                    recipe = recipe,
                    isSaved = favoriteRecipes.any { it.id == recipe.id }
                )
            }
        }
    }
}