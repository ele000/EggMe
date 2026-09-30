package com.example.myapplication.domain

import com.example.myapplication.domain.UserRepository
import com.example.myapplication.domain.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetTrySomethingNewRecipesUseCase(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) {
    //Remove recipes already cooked and own recipes to let user find new recipe
    operator fun invoke(userId: String?): Flow<List<RecipeWithSaved>> {
        if (userId == null) {
            return recipeRepository.getRecipes().map { recipes ->
                recipes.shuffled().map { recipe ->
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
            val cookedRecipes = userLists?.get("Executed") ?: emptyList()

            val filteredRecipes = recipes.filter { recipe ->
                !(cookedRecipes.any { it.id == recipe.id } ||
                        recipe.author.id == userId)
            }

            filteredRecipes.map { recipe ->
                RecipeWithSaved(
                    recipe = recipe,
                    isSaved = favoriteRecipes.any { it.id == recipe.id }
                )
            }
        }
    }
}