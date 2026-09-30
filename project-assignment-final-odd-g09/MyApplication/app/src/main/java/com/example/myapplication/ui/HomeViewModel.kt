package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Notification
import com.example.myapplication.data.Recipe
import com.example.myapplication.data.auth.SessionManagerFacade
import com.example.myapplication.domain.DeleteReadNotificationUseCase
import com.example.myapplication.domain.GetMostLovedRecipesUseCase
import com.example.myapplication.domain.GetNotificationsByUserUseCase
import com.example.myapplication.domain.GetPickedForYouRecipesUseCase
import com.example.myapplication.domain.GetTodayBestRecipesUseCase
import com.example.myapplication.domain.GetTrySomethingNewRecipesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.example.myapplication.domain.RecipeWithSaved
import com.example.myapplication.domain.ToggleFavoriteRecipeUseCase
import com.example.myapplication.domain.UpdateNotificationReadUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


class HomeViewModel(
    private val toggleFavoriteRecipeUseCase: ToggleFavoriteRecipeUseCase,
    private val getPickedForYouRecipesUseCase: GetPickedForYouRecipesUseCase,
    private val getMostLovedRecipesUseCase: GetMostLovedRecipesUseCase,
    private val getTodayBestRecipesUseCase: GetTodayBestRecipesUseCase,
    private val getTrySomethingNewRecipesUseCase: GetTrySomethingNewRecipesUseCase,
) : ViewModel() {
    private val _recipesPickedForYou = MutableStateFlow<List<RecipeWithSaved>>(emptyList())
    val recipesPickedForYou: StateFlow<List<RecipeWithSaved>> = _recipesPickedForYou.asStateFlow()

    private val _recipesMostLoved = MutableStateFlow<List<RecipeWithSaved>>(emptyList())
    val recipesMostLoved: StateFlow<List<RecipeWithSaved>> = _recipesMostLoved.asStateFlow()

    private val _recipesTodayBest = MutableStateFlow<List<RecipeWithSaved>>(emptyList())
    val recipesTodayBest: StateFlow<List<RecipeWithSaved>> = _recipesTodayBest.asStateFlow()

    private val _recipesTrySomethingNew = MutableStateFlow<List<RecipeWithSaved>>(emptyList())
    val recipesTrySomethingNew: StateFlow<List<RecipeWithSaved>> = _recipesTrySomethingNew.asStateFlow()



    //private val _lastNotificationForSnackbar = MutableStateFlow<Notification?>(null)
    //val lastNotificationForSnackbar = _lastNotificationForSnackbar.asStateFlow()



    private var job: Job? = null

    private var jobList1: Job? = null
    private var jobList2: Job? = null
    private var jobList3: Job? = null
    private var jobList4: Job? = null

    init {
        job = viewModelScope.launch {
            SessionManagerFacade.currentUserStateFlow.collect { userId ->

                loadRecipes(userId)

            }
        }
    }

    private fun loadRecipes(userId: String?) {
        jobList1?.cancel()
        jobList1 = viewModelScope.launch {
            getPickedForYouRecipesUseCase(userId).collect { recipeListWithSaved ->
                _recipesPickedForYou.update { recipeListWithSaved }
            }
        }

        jobList2?.cancel()
        jobList2 = viewModelScope.launch {
            getMostLovedRecipesUseCase(userId).collect { recipeListWithSaved ->
                _recipesMostLoved.update { recipeListWithSaved }
            }
        }

        jobList3?.cancel()
        jobList3 = viewModelScope.launch {
            getTodayBestRecipesUseCase(userId).collect { recipeListSaved ->
                _recipesTodayBest.update { recipeListSaved }
            }
        }

        jobList4?.cancel()
        jobList4 = viewModelScope.launch {
            getTrySomethingNewRecipesUseCase(userId).collect { recipeListSaved ->
                _recipesTrySomethingNew.update { recipeListSaved }
            }
        }
    }

    ///



    fun onToggleFavorite(recipe: Recipe, onAuthRequired: () -> Unit): Boolean {
        if (SessionManagerFacade.isLoggedIn) {
            viewModelScope.launch {
                SessionManagerFacade.currentUserId?.let { userId ->
                    toggleFavoriteRecipeUseCase(userId, recipe)
                }
            }
            return true
        } else {
            onAuthRequired()
            return false
        }
    }



    companion object {
        fun provideFactory(
            toggleFavoriteRecipeUseCase: ToggleFavoriteRecipeUseCase,
            getPickedForYouRecipesUseCase: GetPickedForYouRecipesUseCase,
            getMostLovedRecipesUseCase: GetMostLovedRecipesUseCase,
            getTodayBestRecipesUseCase: GetTodayBestRecipesUseCase,
            getTrySomethingNewRecipesUseCase: GetTrySomethingNewRecipesUseCase,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(
                        toggleFavoriteRecipeUseCase,
                        getPickedForYouRecipesUseCase,
                        getMostLovedRecipesUseCase,
                        getTodayBestRecipesUseCase,
                        getTrySomethingNewRecipesUseCase
                    ) as T
                }
            }
    }
}
