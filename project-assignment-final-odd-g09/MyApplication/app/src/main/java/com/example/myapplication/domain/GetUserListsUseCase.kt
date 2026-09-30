package com.example.myapplication.domain


import com.example.myapplication.data.Recipe
import com.example.myapplication.data.RecipeForCard
import com.example.myapplication.data.toRecipeForCard
import com.example.myapplication.domain.UserRepository
import com.example.myapplication.domain.RecipeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn


//https://www.youtube.com/watch?v=gIhjCh3U88I at 6:42


class GetUserListsUseCase (
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
){
    operator fun invoke(userId: String): Flow<Map<String, List<RecipeForCard>>> {
        return combine(
            recipeRepository.getRecipesByOwner(userId),
            userRepository.getUserLists(userId)
        ) { recipes, lists ->
            buildMap {
                put("My recipes", recipes.map { it.toRecipeForCard() })
                putAll(lists ?: emptyMap())
            }
        }.flowOn(defaultDispatcher)
    }
}


