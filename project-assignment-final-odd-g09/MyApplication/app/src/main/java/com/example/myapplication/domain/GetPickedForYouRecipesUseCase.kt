package com.example.myapplication.domain


import com.example.myapplication.data.Recipe
import com.example.myapplication.data.User
import com.example.myapplication.domain.UserRepository
import com.example.myapplication.domain.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetPickedForYouRecipesUseCase(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository
) {
    //Show recipes that fit the user's restrictions and contains at least one user's favourite ingredient
    operator fun invoke(userId: String?): Flow<List<RecipeWithSaved>> {
        if (userId == null) {
            return recipeRepository.getRecipes().map { recipes ->
                recipes.shuffled().map { recipe ->
                    RecipeWithSaved(recipe = recipe, isSaved = false)
                }
            }
        }
        return combine(
            recipeRepository.getRecipes(),
            userRepository.getUser(userId)
        ) { recipes, user ->
            val favoriteRecipes = user?.userLists?.get("Favorite") ?: emptyList()
            val userRestriction = user?.restriction ?: emptyList()
            val userFavoriteIngredients = user?.favoriteIngredients ?: emptyList()
            var filteredRecipes = emptyList<Recipe>()

            if (userFavoriteIngredients.isEmpty()) {
                filteredRecipes = recipes.filter { recipe ->
                    ( userRestriction.all { it in recipe.dietaryRestrictions }
                            /*&& recipe.author.id != userId*/) //removed this check since there are not so many recipes
                }
            } else {
                filteredRecipes = recipes.filter { recipe ->
                    ( userRestriction.all { it in recipe.dietaryRestrictions }
                            && userFavoriteIngredients.any { it in recipe.ingredients.map { it.name } }
                            /*&& recipe.author.id != userId*/)
                }
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