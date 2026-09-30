package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.auth.SessionManagerFacade
import com.example.myapplication.data.Cost
import com.example.myapplication.data.Difficulty
import com.example.myapplication.data.Recipe
import com.example.myapplication.data.User
import com.example.myapplication.domain.GetRecipesUseCase
import com.example.myapplication.domain.GetUserRecipesUseCase
import com.example.myapplication.domain.IngredientRepository
import com.example.myapplication.domain.RecipeRepository
import com.example.myapplication.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.example.myapplication.domain.MetadataRepository
import com.example.myapplication.domain.RecipeWithSaved
import com.example.myapplication.domain.ToggleFavoriteRecipeUseCase

private enum class RecipeListScope {
    ALL,
    OWNED
}

private const val FIXED_EGGS_FILTER = "EGGS"
private const val MAX_TIME_MINUTES = 23 * 60 + 59

class RecipeListViewModel(
    private val userRepository: UserRepository,
    private val ingredientRepository: IngredientRepository,
    private val metadataRepository: MetadataRepository,
    private val toggleFavoriteRecipeUseCase: ToggleFavoriteRecipeUseCase,
    private val getRecipesUseCase: GetRecipesUseCase
) : ViewModel() {
    private val _recipes = MutableStateFlow<List<RecipeWithSaved>>(emptyList())
    val recipes: StateFlow<List<RecipeWithSaved>> = _recipes.asStateFlow()

    private val _filteredRecipes = MutableStateFlow<List<RecipeWithSaved>>(emptyList())
    val filteredRecipes: StateFlow<List<RecipeWithSaved>> = _filteredRecipes.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _ingredientFilters = MutableStateFlow<List<String>>(listOf(FIXED_EGGS_FILTER))
    val ingredientFilters: StateFlow<List<String>> = _ingredientFilters.asStateFlow()

    private val _availableIngredients = MutableStateFlow<List<String>>(listOf(FIXED_EGGS_FILTER))
    val availableIngredients: StateFlow<List<String>> = _availableIngredients.asStateFlow()

    private val _selectedCostFilter = MutableStateFlow<Cost?>(null)
    val selectedCostFilter: StateFlow<Cost?> = _selectedCostFilter.asStateFlow()

    private val _selectedDifficultyFilter = MutableStateFlow<Difficulty?>(null)
    val selectedDifficultyFilter: StateFlow<Difficulty?> = _selectedDifficultyFilter.asStateFlow()

    private val _maxTimeMinutes = MutableStateFlow(0)
    val maxTimeMinutes: StateFlow<Int> = _maxTimeMinutes.asStateFlow()

    private val _maxTimeLabel = MutableStateFlow("00h:00m")
    val maxTimeLabel: StateFlow<String> = _maxTimeLabel.asStateFlow()

    private val _availableCuisines = MutableStateFlow<List<String>>(emptyList())
    val availableCuisines: StateFlow<List<String>> = _availableCuisines.asStateFlow()

    private val _availableRestrictions = MutableStateFlow<List<String>>(emptyList())
    val availableRestrictions: StateFlow<List<String>> = _availableRestrictions.asStateFlow()

    private val _selectedCuisineFilter = MutableStateFlow<String?>(null)
    val selectedCuisineFilter: StateFlow<String?> = _selectedCuisineFilter.asStateFlow()

    private var job: Job? = null
    private var userJob: Job? = null
    private var ingredientJob: Job? = null

    init {
        loadRecipes()
        loadAvailableIngredients()
        loadMetadata()
    }


    private fun loadRecipes() {
        userJob = viewModelScope.launch {
            SessionManagerFacade.currentUserStateFlow.collect { userId ->
                job?.cancel()

                job = viewModelScope.launch {
                    getRecipesUseCase(userId).collect { recipeList ->
                        _recipes.update { recipeList }
                        applyFilters()
                    }
                }

            }
        }
    }

    private fun loadAvailableIngredients() {
        ingredientJob?.cancel()
        ingredientJob = viewModelScope.launch {
            ingredientRepository.getIngredients().collect { ingredients ->
                val normalized= ingredients
                    .map { it.name.trim().uppercase() }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()

                val updatedIngredients = if (normalized.contains(FIXED_EGGS_FILTER)) {
                    normalized
                } else {
                    listOf(FIXED_EGGS_FILTER) + normalized
                }
                _availableIngredients.update { updatedIngredients }
            }
        }
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            metadataRepository.getCuisines().collect { list ->
                _availableCuisines.update { list }
            }
        }
        viewModelScope.launch {
            metadataRepository.getDietaryRestrictions().collect { list ->
                _availableRestrictions.update { list }
            }
        }
    }

    fun addIngredientFilter(ingredient: String) {
        if (ingredient.isBlank()) return
        val normalized = ingredient.trim().uppercase()
        var hasChanged = false
        _ingredientFilters.update { current ->
            if (current.contains(normalized)) {
                current
            } else {
                hasChanged = true
                current + normalized
            }
        }
        if (hasChanged) {
            applyFilters()
        }
    }

    fun removeIngredientFilter(ingredient: String) {
        val normalized = ingredient.trim().uppercase()
        if (normalized == FIXED_EGGS_FILTER) return
        var hasChanged = false
        _ingredientFilters.update { current ->
            val updated = current.filterNot { it == normalized }
            hasChanged = updated != current
            updated
        }
        if (hasChanged) {
            applyFilters()
        }
    }

    fun setCostFilter(cost: Cost) {
        _selectedCostFilter.update { current -> if (current == cost) null else cost }
        applyFilters()
    }

    fun setDifficultyFilter(difficulty: Difficulty) {
        _selectedDifficultyFilter.update { current -> if (current == difficulty) null else difficulty }
        applyFilters()
    }

    fun setCuisineFilter(cuisine: String?) {
        _selectedCuisineFilter.update { if (it == cuisine) null else cuisine }
        applyFilters()
    }


    fun clearFilters() {
        _ingredientFilters.update { listOf(FIXED_EGGS_FILTER) }
        _selectedCostFilter.update { null }
        _selectedDifficultyFilter.update { null }
        _selectedCuisineFilter.update { null }
        _maxTimeMinutes.update { 0 }
        _maxTimeLabel.update { formatMinutesLabel(0) }
        applyFilters()
    }

    fun setMaxTimeFilter(minutes: Int) {
        val normalizedMinutes = when {
            minutes < 0 -> 0
            minutes > MAX_TIME_MINUTES -> MAX_TIME_MINUTES
            else -> minutes
        }
        _maxTimeMinutes.update { normalizedMinutes }
        _maxTimeLabel.update { formatMinutesLabel(normalizedMinutes) }
        applyFilters()
    }

    private fun formatMinutesLabel(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return String.format("%02dh:%02dm", hours, remainingMinutes)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.update { query }
        applyFilters()
    }

    private fun applyFilters() {
        _filteredRecipes.update {
            _recipes.value
                .filterByIngredients(_ingredientFilters.value)
                .filterByCost(_selectedCostFilter.value)
                .filterByDifficulty(_selectedDifficultyFilter.value)
                .filterByMaxTime(_maxTimeMinutes.value)
                .filterBySearchQuery(_searchQuery.value)
                .filterByCuisine(_selectedCuisineFilter.value)
        }
    }

    fun onToggleFavorite (recipe: Recipe,onAuthRequired: () -> Unit): Boolean{
        if(SessionManagerFacade.isLoggedIn){
            viewModelScope.launch {
                SessionManagerFacade.currentUserId?.let { userId ->
                    toggleFavoriteRecipeUseCase(userId, recipe)
                }
            }
            return true
        }
        else {
            onAuthRequired()
            return false
        }
    }

    private fun List<RecipeWithSaved>.filterByCuisine(cuisine: String?): List<RecipeWithSaved> {
        if (cuisine == null) return this
        return filter { it.recipe.cuisineType.equals(cuisine, ignoreCase = true) }
    }


    private fun List<RecipeWithSaved>.filterByIngredients(filters: List<String>): List<RecipeWithSaved> {
        if (filters.isEmpty()) return this
        return filter { recipeWithSaved ->
            val recipeIngredients = recipeWithSaved.recipe.ingredients
                .map { it.name.trim().uppercase() }
                .toSet()
            filters.all { filter -> filter.trim().uppercase() in recipeIngredients }
        }
    }

    private fun List<RecipeWithSaved>.filterByCost(cost: Cost?): List<RecipeWithSaved> {
        if (cost == null) return this
        return filter { it.recipe.cost == cost }
    }

    private fun List<RecipeWithSaved>.filterByDifficulty(difficulty: Difficulty?): List<RecipeWithSaved> {
        if (difficulty == null) return this
        return filter { it.recipe.difficulty == difficulty }
    }

    private fun List<RecipeWithSaved>.filterByMaxTime(maxTimeMinutes: Int): List<RecipeWithSaved> {
        if (maxTimeMinutes <= 0) return this
        return filter { it.recipe.time <= maxTimeMinutes }
    }

    private fun List<RecipeWithSaved>.filterBySearchQuery(query: String): List<RecipeWithSaved> {
        if (query.isBlank()) return this
        val normalized = query.trim()
        return filter { recipeWithSaved ->
            recipeWithSaved.recipe.title.contains(normalized, ignoreCase = true)
        }
    }

    companion object {
        fun provideFactory(
            userRepositoryFactored: UserRepository,
            ingredientRepositoryFactored: IngredientRepository,
            metadataRepositoryFactored: MetadataRepository,
            toggleFavoriteRecipeUseCase: ToggleFavoriteRecipeUseCase,
            getRecipesUseCase: GetRecipesUseCase,
            getUserRecipesUseCase: GetUserRecipesUseCase
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RecipeListViewModel(
                        userRepositoryFactored,
                        ingredientRepositoryFactored,
                        metadataRepositoryFactored,
                        toggleFavoriteRecipeUseCase,
                        getRecipesUseCase,
                    ) as T
                }
            }
    }
}
