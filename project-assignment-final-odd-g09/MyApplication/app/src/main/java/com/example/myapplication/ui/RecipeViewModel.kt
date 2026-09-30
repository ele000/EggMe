package com.example.myapplication.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.auth.SessionManagerFacade
import com.example.myapplication.data.Cost
import com.example.myapplication.data.Difficulty
import com.example.myapplication.data.Ingredient
import com.example.myapplication.data.Recipe
import com.example.myapplication.data.Step
import com.example.myapplication.data.User
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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.example.myapplication.data.Author
import com.example.myapplication.data.IngredientOption
import com.example.myapplication.domain.RemoveRecipeFromUserListUseCase
import com.example.myapplication.domain.SaveRecipeInUserListUseCase
import kotlinx.coroutines.flow.combine
import com.example.myapplication.data.Review
import com.example.myapplication.data.toAuthor
import com.example.myapplication.ui.navigation.RecipeDetailRoute
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class RecipeFormValidation(
    val titleError: String = "",
    val timeError: String = "",
    val servingsError: String = "",
    val ingredientsError: String = "",
    val stepsError: String = "",
    val pictureError: String = "",
    val eggError: String = "",
    val isValid: Boolean = true
)

data class RecipeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val inputTitle: String = "",
    val inputPicture: String = "",
    val inputCost: Cost = Cost.CHEAP,
    val inputDifficulty: Difficulty = Difficulty.EASY,
    val inputCuisine: String = "Italian",
    val inputTime: Int = 0,
    val inputTimeLabel: String = "00h:00m",
    val inputServings: String = "",
    val inputIngredients: List<Ingredient> = listOf(Ingredient("Eggs", 1, "pcs")),
    val inputSteps: List<Step> = listOf(Step("", "", "")),
    val inputRestrictions: List<String> = emptyList(),
    val validation: RecipeFormValidation = RecipeFormValidation(),
    val availableCuisines: List<String> = emptyList(),
    val availableRestrictions: List<String> = emptyList()
) {
    val isAddEnabled: Boolean get() = validation.isValid && !isLoading
}

///////
data class ReviewFormValidation(
    val descriptionError: String = "",
    val ratingError: String = "",
    val isValid: Boolean = true
)

data class ReviewUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val inputDescription: String = "",
    val inputRating: Int = 0,
    val inputPhoto: String = "",
    val validation: ReviewFormValidation = ReviewFormValidation()
)

class RecipeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val recipeRepository: RecipeRepository,
    private val ingredientRepository: IngredientRepository,
    private val userRepository: UserRepository,
    private val metadataRepository: MetadataRepository,
    private val saveRecipeInUserListUseCase: SaveRecipeInUserListUseCase,
    private val removeRecipeFromUserListUseCase: RemoveRecipeFromUserListUseCase
) : ViewModel() {
    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _moreActionsExpanded = MutableStateFlow(false)
    val moreActionsExpanded = _moreActionsExpanded.asStateFlow()

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _isCooked = MutableStateFlow(false)
    val isCooked: StateFlow<Boolean> = _isCooked.asStateFlow()

    private val _availableIngredients = MutableStateFlow<List<IngredientOption>>(emptyList())
    val availableIngredients: StateFlow<List<IngredientOption>> = _availableIngredients.asStateFlow()

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _newRecipe = MutableStateFlow(RecipeUiState())
    val newRecipe: StateFlow<RecipeUiState> = _newRecipe.asStateFlow()

    private val _idRecipeCreated = MutableStateFlow("")
    val idRecipeCreated: StateFlow<String> = _idRecipeCreated.asStateFlow()

    private val _newReview = MutableStateFlow(ReviewUiState())
    val newReview: StateFlow<ReviewUiState> = _newReview.asStateFlow()

    private val _reviewCreated = MutableStateFlow(false)
    val reviewCreated: StateFlow<Boolean> = _reviewCreated.asStateFlow()

    private val _shareEvent = MutableSharedFlow<String>()
    val shareEvent = _shareEvent.asSharedFlow()

    private var job: Job? = null
    private var ingredientJob: Job? = null
    private var userJob: Job? = null
    private var user2Job: Job? = null
    private var savedStateJob: Job? = null
    private var cookedStateJob: Job? = null


    init {
        loadIngredients()
        loadCurrentUser()
        loadMetadata()
        loadSavedState()
        loadCookedState()

        try {
            val detailRoute = savedStateHandle.toRoute<RecipeDetailRoute>()
            loadRecipe(detailRoute.recipeId)
        } catch (e: Exception) {
            clearRecipe()
        }
    }


    fun increment() {
        _count.update { it + 1 }
    }

    fun decrement() {
        _count.update { currentCount ->
            if (currentCount <= 1) currentCount
            else currentCount - 1
        }
    }

    fun loadRecipe(idRecipe: String?) = loadTheRecipe(idRecipe)
    private fun loadTheRecipe(idRecipe: String? = null) {
        job?.cancel()
        job = viewModelScope.launch {
            if (idRecipe != null)
                recipeRepository.getRecipe(idRecipe).collect { recipe ->
                    _recipe.update { recipe }

                    _count.value = recipe?.servings ?: 0
                }
        }
    }

    fun loadIngredients() {
        ingredientJob?.cancel()

        ingredientJob = viewModelScope.launch {
            ingredientRepository.getIngredients()
                .collect { list ->
                    _availableIngredients.value = list
                }
        }
    }

    private fun loadCurrentUser() {
        userJob?.cancel()
        userJob = viewModelScope.launch {
            SessionManagerFacade.currentUserStateFlow.collect { userId ->
                user2Job?.cancel()
                if (userId != null) {
                    user2Job = launch {
                        userRepository.getUser(userId).collect { user ->
                            _currentUser.update { user }
                        }
                    }
                } else {
                    _currentUser.update { null }
                }
            }
        }
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            metadataRepository.getCuisines().collect { list ->
                _newRecipe.update { it.copy(availableCuisines = list) }
            }
        }
        viewModelScope.launch {
            metadataRepository.getDietaryRestrictions().collect { list ->
                _newRecipe.update { it.copy(availableRestrictions = list) }
            }
        }
    }

    fun clearRecipe() {
        _recipe.update { null }
        _isEditing.update { false }
        _isImporting.update { false }
    }

    fun enableEditMode() {
        val current = _recipe.value ?: return

        _newRecipe.update { currentState ->
            RecipeUiState(
                availableCuisines = currentState.availableCuisines,
                availableRestrictions = currentState.availableRestrictions,
                inputTitle = current.title,
                inputPicture = current.recipePicture,
                inputCost = current.cost,
                inputDifficulty = current.difficulty,
                inputCuisine = current.cuisineType,
                inputTime = current.time,
                inputTimeLabel = formatMinutesLabel(current.time),
                inputServings = current.servings.toString(),
                inputIngredients = current.ingredients,
                inputSteps = current.steps,
                inputRestrictions = current.dietaryRestrictions ?: emptyList()
            )
        }

        _isEditing.update { true }
    }

    fun cancelEdit() {
        _isEditing.update { false }
    }

    fun showMoreActions() {
        _moreActionsExpanded.update { true }
    }

    fun hideMoreActions() {
        _moreActionsExpanded.update { false }
    }


    //IMPORT

    fun importRecipe(onAuthRequired: () -> Unit) {
        if(SessionManagerFacade.isLoggedIn){
            val current = _recipe.value ?: return

            _newRecipe.update { currentState ->
                RecipeUiState(
                    availableCuisines = currentState.availableCuisines,
                    availableRestrictions = currentState.availableRestrictions,
                    inputTitle = current.title,
                    inputPicture = current.recipePicture,
                    inputCost = current.cost,
                    inputDifficulty = current.difficulty,
                    inputCuisine = current.cuisineType,
                    inputTime = current.time,
                    inputTimeLabel = formatMinutesLabel(current.time),
                    inputServings = current.servings.toString(),
                    inputIngredients = current.ingredients,
                    inputSteps = current.steps,
                    inputRestrictions = current.dietaryRestrictions ?: emptyList(),
                    isLoading = false
                )
            }

            _isImporting.update { true }

        }
        else{
            onAuthRequired()
        }

    }

    //CREATE RECIPE (also used during editing and importing recipe)
    fun resetIdRecipeCreated() {
        _idRecipeCreated.update { "" }
    }

    fun updateTitle(title: String) {
        _newRecipe.update { it.copy(inputTitle = title) }
    }

    fun updatePicture(picture: String) {
        _newRecipe.update { it.copy(inputPicture = picture) }
    }

    fun updateCost(cost: Cost) {
        _newRecipe.update { it.copy(inputCost = cost) }
    }

    fun updateDifficulty(difficulty: Difficulty) {
        _newRecipe.update { it.copy(inputDifficulty = difficulty) }
    }

    fun updateCuisine(cuisine: String) {
        _newRecipe.update { it.copy(inputCuisine = cuisine) }
    }

    fun updateTime(time: Int) {
        _newRecipe.update {
            it.copy(
                inputTime = time,
                inputTimeLabel = formatMinutesLabel(time)
            )
        }
    }

    private fun formatMinutesLabel(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return String.format("%02dh:%02dm", hours, remainingMinutes)
    }

    fun updateServings(servings: String) {
        _newRecipe.update { it.copy(inputServings = servings) }
    }

    fun updateRestrictions(restriction: String) {
        _newRecipe.update { currentRecipe ->
            val newRestrictionsList = if (restriction in currentRecipe.inputRestrictions) {
                currentRecipe.inputRestrictions - restriction
            } else {
                currentRecipe.inputRestrictions + restriction
            }

            currentRecipe.copy(inputRestrictions = newRestrictionsList)
        }
    }

    fun addStep() {
        _newRecipe.update {
            it.copy(inputSteps = it.inputSteps + Step("", "", ""))
        }
    }

    fun updateStep(index: Int, step: Step) {
        _newRecipe.update { currentRecipe ->
            val newStepsList = currentRecipe.inputSteps.toMutableList()
            newStepsList[index] = step

            currentRecipe.copy(inputSteps = newStepsList)
        }
    }

    fun updateStepImage(index: Int, imageUrl: String) {
        val currentStep = _newRecipe.value.inputSteps.getOrNull(index)
        currentStep?.let { step ->
            updateStep(index, step.copy(picture = imageUrl))
        }
    }

    fun removeStep(index: Int) {
        _newRecipe.update { currentRecipe ->
            val newStepsList = currentRecipe.inputSteps.toMutableList()
            newStepsList.removeAt(index)

            currentRecipe.copy(inputSteps = newStepsList)
        }
    }

    fun addIngredient() {
        _newRecipe.update {
            it.copy(inputIngredients = it.inputIngredients + Ingredient("", 0, ""))
        }
    }

    fun selectIngredient(index: Int, ingredient: IngredientOption) {
        _newRecipe.update { currentRecipe ->
            val newIngredientsList = currentRecipe.inputIngredients.toMutableList()
            val currentQuantity = newIngredientsList[index].quantity

            newIngredientsList[index] = Ingredient(ingredient.name,currentQuantity,ingredient.unityOfMeasurement)//ingredient.copy(quantity = currentQuantity)

            currentRecipe.copy(inputIngredients = newIngredientsList)
        }
    }

    fun updateIngredient(index: Int, quantity: Int) {
        _newRecipe.update { currentRecipe ->
            val newIngredientsList = currentRecipe.inputIngredients.toMutableList()

            newIngredientsList[index] = newIngredientsList[index].copy(quantity = quantity)

            currentRecipe.copy(inputIngredients = newIngredientsList)
        }
    }

    fun removeIngredient(index: Int) {
        _newRecipe.update { currentRecipe ->
            val newIngredientsList = currentRecipe.inputIngredients.toMutableList()

            newIngredientsList.removeAt(index)

            currentRecipe.copy(inputIngredients = newIngredientsList)
        }
    }


    ////validation
    private fun validateRecipe(state: RecipeUiState): RecipeFormValidation {

        var titleError = ""
        var timeError = ""
        var servingsError = ""
        var ingredientsError = ""
        var stepsError = ""
        var pictureError = ""
        var eggError = ""

        if (state.inputTitle.isBlank()) {
            titleError = "Title is required"
        }

        if (state.inputTime <= 0) {
            timeError = "Invalid time"
        }

        if (state.inputServings.toIntOrNull() == null || state.inputServings.toInt() <= 0) {
            servingsError = "Invalid servings"
        }

        if (state.inputIngredients[0].quantity <= 0) {
            eggError = "Add at least 1 egg"
        }

        state.inputIngredients.drop(1).forEach { ing ->
            if (ing.quantity <= 0 || ing.name.isBlank()) {
                ingredientsError = "Invalid ingredient detected"
            }
        }

        if (state.inputSteps.isEmpty()) {
            stepsError = "Add at least 1 step"
        }

        state.inputSteps.forEach { s ->
            if (s.title.isBlank() || s.picture.isBlank() || s.description.isBlank()) {
                stepsError = "Invalid step detected"
            }
        }

        if (state.inputPicture.isEmpty()) {
            pictureError = "Add recipe picture"
        }

        val isValid = titleError.isBlank()
                && timeError.isBlank()
                && servingsError.isBlank()
                && ingredientsError.isBlank()
                && stepsError.isBlank()
                && pictureError.isBlank()
                && eggError.isBlank()

        return RecipeFormValidation(
            titleError,
            timeError,
            servingsError,
            ingredientsError,
            stepsError,
            pictureError,
            eggError,
            isValid
        )
    }

    //add, import or edit recipe
    fun addRecipe() {
        val state = _newRecipe.value
        val validation = validateRecipe(state)

        if (!validation.isValid) {
            _newRecipe.update { it.copy(validation = validation) }
            return
        }

        //if (state.inputTitle.isBlank() || state.inputIngredients.isEmpty() || state.inputSteps.isEmpty()) return

        viewModelScope.launch {
            _newRecipe.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                var finalCoverUrl = state.inputPicture
                if (finalCoverUrl.startsWith("content://") || finalCoverUrl.startsWith("file://")) {
                    val uri = android.net.Uri.parse(finalCoverUrl)
                    finalCoverUrl = com.example.myapplication.data.StorageRepository.uploadImage(uri)
                }

                val finalSteps = state.inputSteps.map { step ->
                    if (step.picture.startsWith("content://") || step.picture.startsWith("file://")) {
                        val uri = android.net.Uri.parse(step.picture)
                        val uploadedUrl = com.example.myapplication.data.StorageRepository.uploadImage(uri)
                        step.copy(picture = uploadedUrl)
                    } else {
                        step
                    }
                }

                if (_isEditing.value) {
                    // Logica di MODIFICA
                    val recipeToUpdate = Recipe(
                        id = _recipe.value!!.id,
                        title = state.inputTitle,
                        recipePicture = finalCoverUrl,
                        cost = state.inputCost,
                        difficulty = state.inputDifficulty,
                        cuisineType = state.inputCuisine,
                        time = state.inputTime,
                        servings = state.inputServings.toIntOrNull() ?: 0,
                        ingredients = state.inputIngredients,
                        steps = finalSteps,
                        dietaryRestrictions = state.inputRestrictions,
                        author = _recipe.value!!.author,
                        rating = _recipe.value!!.rating,
                        reviews = _recipe.value!!.reviews,
                        creationDate = _recipe.value!!.creationDate
                    )

                    recipeRepository.updateRecipe(recipeToUpdate)
                    _recipe.update { recipeToUpdate }
                    resetRecipe()
                    _isEditing.update { false }
                    _newRecipe.update { it.copy(isLoading = false) }

                } else {
                    val wasImporting = _isImporting.value

                    val recipeToCreate = Recipe(
                        id = "",
                        title = state.inputTitle,
                        recipePicture = finalCoverUrl,
                        cost = state.inputCost,
                        difficulty = state.inputDifficulty,
                        cuisineType = state.inputCuisine,
                        time = state.inputTime,
                        servings = state.inputServings.toIntOrNull() ?: 0,
                        ingredients = state.inputIngredients,
                        steps = finalSteps,
                        dietaryRestrictions = state.inputRestrictions,
                        author = currentUser.value?.toAuthor() ?: Author(),
                        rating = 0.0,
                        creationDate = Timestamp.now(),
                        forkedFromUserId = if (wasImporting) _recipe.value?.author?.id else ""
                    )

                    val createdRecipeId = recipeRepository.addRecipe(recipeToCreate)
                    _recipe.update { recipeToCreate.copy(id = createdRecipeId) }

                    if (wasImporting) {
                        loadRecipe(createdRecipeId)
                        _idRecipeCreated.update { "" }
                    } else {
                        _idRecipeCreated.update { createdRecipeId }
                    }

                    resetRecipe()
                    _isImporting.update { false }
                    _newRecipe.update { it.copy(isLoading = false) }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _newRecipe.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "An error occurred while saving the recipe or uploading images."
                    )
                }
            }
        }
    }

    fun resetRecipe() {
        _newRecipe.update { currentState ->
            RecipeUiState(
                availableCuisines = currentState.availableCuisines,
                availableRestrictions = currentState.availableRestrictions
            )
        }
    }

    //delete
    fun deleteRecipe() {
        viewModelScope.launch {
            _newRecipe.update { it.copy(isLoading = true) }
            try {
                _recipe.value?.let { recipe ->

                    recipeRepository.deleteRecipe(recipe.id)

                    _recipe.update { null }
                    resetRecipe()
                }
            } catch (e: Exception) {
                _newRecipe.update { it.copy(errorMessage = "An error occurred while deleting the recipe") }
            } finally {
                _newRecipe.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSaveClick(onAuthRequired: () -> Unit) {
        if(SessionManagerFacade.isLoggedIn){
            viewModelScope.launch {
                if (_isSaved.value) {
                    removeRecipeFromUserListUseCase(
                        _currentUser.value!!.id,
                        "Favorite",
                        _recipe.value!!.id
                    )
                } else {
                    saveRecipeInUserListUseCase(
                        _currentUser.value!!.id,
                        "Favorite",
                        _recipe.value!!
                    )
                }
            }
        }
        else{
            onAuthRequired()
        }

    }

    fun onCookClick(onAuthRequired: () -> Unit)  {
        if(SessionManagerFacade.isLoggedIn){
            viewModelScope.launch {
                if (_isCooked.value) {
                    removeRecipeFromUserListUseCase(
                        _currentUser.value!!.id,
                        "Executed",
                        _recipe.value!!.id
                    )
                } else {
                    saveRecipeInUserListUseCase(
                        _currentUser.value!!.id,
                        "Executed",
                        _recipe.value!!
                    )
                }
            }
        }
        else{
            onAuthRequired()
        }

    }

    private fun loadSavedState() {
        savedStateJob?.cancel()
        savedStateJob = viewModelScope.launch {
            combine(
                currentUser,
                recipe
            ) { user, recipeVal ->
                user?.userLists?.get("Favorite")?.any { it.id == recipeVal?.id } == true
            }.collect { isSavedValue ->
                _isSaved.update { isSavedValue }
            }
        }
    }

    private fun loadCookedState() {
        cookedStateJob?.cancel()
        cookedStateJob = viewModelScope.launch {
            combine(
                currentUser,
                recipe
            ) { user, recipeVal ->
                user?.userLists?.get("Executed")?.any { it.id == recipeVal?.id } == true
            }.collect { isCookedValue ->
                _isCooked.update { isCookedValue }
            }
        }
    }

    //reviews
    private fun validateReview(state: ReviewUiState): ReviewFormValidation {
        var descriptionError = ""
        var ratingError = ""

        if(state.inputDescription.isBlank()){
            descriptionError = "Description is required"
        }

        if(state.inputRating<=0){
            ratingError = "Rating is required"
        }

        val isValid = descriptionError.isBlank() && ratingError.isBlank()

        return ReviewFormValidation(
            descriptionError,
            ratingError,
            isValid
        )
    }

    fun updateReviewDescription(description: String){
        _newReview.update { it.copy(inputDescription = description) }
    }

    fun updateReviewRating(rating: Int){
        _newReview.update { it.copy(inputRating = rating) }
    }

    fun updateReviewPhoto(photo: String){
        _newReview.update { it.copy(inputPhoto = photo) }
    }

    fun addReview(){
        val state = _newReview.value
        val currentRecipe = _recipe.value ?: return
        val validation = validateReview(state)

        if(!validation.isValid){
            _newReview.update { it.copy(validation = validation) }
            return
        }

        viewModelScope.launch {
            _newReview.update { it.copy(isLoading = true, errorMessage = null) }

            try{
                var finalPhotoUrl = state.inputPhoto
                if (finalPhotoUrl.startsWith("content://") || finalPhotoUrl.startsWith("file://")) {
                    val uri = android.net.Uri.parse(finalPhotoUrl)
                    finalPhotoUrl = com.example.myapplication.data.StorageRepository.uploadImage(uri)
                }

                val review = Review(
                    rating = state.inputRating,
                    description = state.inputDescription,
                    photo = finalPhotoUrl,
                    reviewDate = Timestamp.now(),
                    author = _currentUser.value?.toAuthor() ?: Author(),
                )

                val newReviewList = currentRecipe.reviews + review

                val recipeToUpdate = currentRecipe.copy(
                    dietaryRestrictions = currentRecipe.dietaryRestrictions ?: emptyList(),
                    reviews = newReviewList
                )

                recipeRepository.updateRecipe(recipeToUpdate)

                _recipe.update { recipeToUpdate }

                resetReview()

                _reviewCreated.update{true}
                _newReview.update { it.copy(isLoading = false) }

            }catch(e: Exception){
                _newReview.update {
                    it.copy(isLoading = false , errorMessage = "Failed to add the review")
                }
            }
        }
    }

    fun resetReviewCreated() {
        _reviewCreated.update{false}
    }

    fun resetReview(){
        _newReview.update {
            it.copy(
                isLoading = false,
                errorMessage = "",
                inputDescription = "",
                inputRating = 0,
                inputPhoto = "",
                validation = ReviewFormValidation()
            )
        }
    }

    fun shareRecipeLink() {
        val currentRecipe = _recipe.value

        if (currentRecipe?.id != null) {
            viewModelScope.launch {
                _shareEvent.emit(currentRecipe.id)
            }
        }
    }


    companion object {
        fun provideFactory(
            recipeRepositoryFactored: RecipeRepository,
            ingredientRepositoryFactored: IngredientRepository,
            userRepositoryFactored: UserRepository,
            metadataRepositoryFactored: MetadataRepository,
            saveRecipeInUserListUseCase: SaveRecipeInUserListUseCase,
            removeRecipeFromUserListUseCase: RemoveRecipeFromUserListUseCase
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                RecipeViewModel(
                    savedStateHandle = savedStateHandle,
                    recipeRepository = recipeRepositoryFactored,
                    ingredientRepository = ingredientRepositoryFactored,
                    userRepository = userRepositoryFactored,
                    metadataRepository = metadataRepositoryFactored,
                    saveRecipeInUserListUseCase = saveRecipeInUserListUseCase,
                    removeRecipeFromUserListUseCase = removeRecipeFromUserListUseCase
                )
            }
        }
    }
}
