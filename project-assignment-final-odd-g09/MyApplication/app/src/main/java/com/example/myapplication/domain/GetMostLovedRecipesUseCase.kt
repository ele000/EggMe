package com.example.myapplication.domain

import com.example.myapplication.data.User
import com.example.myapplication.domain.UserRepository
import com.example.myapplication.domain.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetMostLovedRecipesUseCase(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) {
    //Sorted by rank descending
    operator fun invoke(userId: String?): Flow<List<RecipeWithSaved>> {
        if (userId == null) {
            return recipeRepository.getRecipes().map { recipes ->
                recipes.sortedByDescending { it.rating }.map { recipe ->
                    RecipeWithSaved(
                        recipe = recipe,
                        isSaved = false
                    )
                }
            }
        }
        return combine(
            recipeRepository.getRecipes(),
            userRepository.getUserLists(userId)
        ) { recipes, userLists ->
            val favoriteRecipes = userLists?.get("Favorite") ?: emptyList()
            recipes.sortedByDescending { it.rating }.map { recipe ->
                RecipeWithSaved(
                    recipe = recipe,
                    isSaved = favoriteRecipes.any { it.id == recipe.id }
                )
            }
        }
    }
}