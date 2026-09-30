package com.example.myapplication.domain

import com.example.myapplication.data.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getRecipes(): Flow<List<Recipe>>

    fun getRecipe(id: String): Flow<Recipe?>

    // Get only the recipes created/owned by a specific user
    fun getRecipesByOwner(ownerId: String): Flow<List<Recipe>>

    // Update/save an existing recipe
    suspend fun updateRecipe(recipe: Recipe)

    //Also used to modify or import a recipe. Return the id of the recipe added
    suspend fun addRecipe(recipe: Recipe): String

    suspend fun deleteRecipe(id: String)
}
