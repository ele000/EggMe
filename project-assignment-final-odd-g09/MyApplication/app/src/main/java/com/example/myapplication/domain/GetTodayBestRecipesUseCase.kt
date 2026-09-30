package com.example.myapplication.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetTodayBestRecipesUseCase(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) {
    //since we have only a few recipes, the logic of this domain is to return recipes sorted by date
    operator fun invoke(userId: String?): Flow<List<RecipeWithSaved>> {
        if (userId == null) {
            return recipeRepository.getRecipes().map { recipes ->
                recipes.sortedByDescending { it.creationDate }.map { recipe ->
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
            recipes.sortedByDescending { it.creationDate }.map { recipe ->
                RecipeWithSaved(
                    recipe = recipe,
                    isSaved = favoriteRecipes.any { it.id == recipe.id }
                )
            }
        }
    }
}