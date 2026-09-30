package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.auth.SessionManagerFacade
import com.example.myapplication.data.User
import com.example.myapplication.domain.IngredientRepository
import com.example.myapplication.domain.MetadataRepository
import com.example.myapplication.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormValidation(
    val nameError: String = "",
    val surnameError: String = "",
    val userNameError: String = "",
    val roleError: String = "",
    val isValid: Boolean = true
)

data class ProfileScreenState(
    val isLoading: Boolean = false,
    val user: User = User(),
    val draftUser: User = User(),
    val isOwner: Boolean = false,
    val isEditing: Boolean = false,
    val validation: FormValidation = FormValidation(),
    val ingredientOptions: List<String> = emptyList(),
    val cuisineOptions: List<String> = emptyList(),
    val restrictionOptions: List<String> = emptyList()
)

class UserViewModel(
    private val userRepository: UserRepository,
    private val metadataRepository: MetadataRepository,
    private val ingredientRepository: IngredientRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileScreenState())
    val uiState: StateFlow<ProfileScreenState> = _uiState.asStateFlow()

    init {
        loadMetadata()
        viewModelScope.launch {
            SessionManagerFacade.currentUserStateFlow.collect { userId ->
                if (userId == null) {
                    _uiState.update {
                        ProfileScreenState(
                            ingredientOptions = it.ingredientOptions,
                            cuisineOptions = it.cuisineOptions,
                            restrictionOptions = it.restrictionOptions
                        )
                    }
                }
            }
        }
    }

    private fun loadMetadata() {

        viewModelScope.launch {
            metadataRepository.getCuisines().collect { list ->
                _uiState.update { it.copy(cuisineOptions = list) }
            }
        }


        viewModelScope.launch {
            metadataRepository.getDietaryRestrictions().collect { list ->
                _uiState.update { it.copy(restrictionOptions = list) }
            }
        }


        viewModelScope.launch {
            ingredientRepository.getIngredients().collect { list ->
                _uiState.update { it.copy(ingredientOptions = list.map { it.name }) }
            }
        }
    }

    fun loadUser(userId: String, isOwner: Boolean) {
        viewModelScope.launch {
            userRepository.getUser(userId).collect { user ->
                user?.let {
                    _uiState.update { state ->
                        state.copy(user = it, isOwner = isOwner)
                    }
                }
            }
        }
    }

    val isUserLoggedIn: StateFlow<Boolean> = SessionManagerFacade.currentUserStateFlow
        .map { it!=null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionManagerFacade.isLoggedIn
        )


    fun edit() {
        _uiState.update { it.copy(isEditing = true, draftUser = it.user) }
    }

    fun setName(value: String) {
        _uiState.update { it.copy(draftUser = it.draftUser.copy(name = value)) }
    }

    fun setSurname(value: String) {
        _uiState.update { it.copy(draftUser = it.draftUser.copy(surname = value)) }
    }

    fun setUserName(value: String) {
        _uiState.update { it.copy(draftUser = it.draftUser.copy(userName = value)) }
    }

    fun setCookingRole(value: String) {
        _uiState.update { it.copy(draftUser = it.draftUser.copy(cookingRole = value)) }
    }

    fun setProfilePicture(value: String) {
        _uiState.update { it.copy(draftUser = it.draftUser.copy(profilePicture = value)) }
    }

    fun validateAndSave() {
        val draft = _uiState.value.draftUser
        var nError = ""
        var sError = ""
        var uError = ""
        var rError = ""

        if (draft.name.isBlank()) nError = "The name can't be blank"
        if (draft.surname.isBlank()) sError = "The surname can't be blank"

        if (draft.userName.isBlank()) {
            uError = "The username is mandatory"
        }
       else if (draft.userName.length < 3) { uError = "Too short" }

        if (draft.cookingRole.isBlank()) rError = "The cooking role can't be blank"

        val isFormValid = nError.isBlank() && sError.isBlank() && uError.isBlank() && rError.isBlank()

        if (isFormValid) {
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                try {
                    var finalProfilePicture = draft.profilePicture
                    if (finalProfilePicture != null && (finalProfilePicture.startsWith("content://") || finalProfilePicture.startsWith("file://"))) {
                        val uri = android.net.Uri.parse(finalProfilePicture)
                        finalProfilePicture = com.example.myapplication.data.StorageRepository.uploadImage(uri)
                    }

                    val userWithUrls = draft.copy(profilePicture = finalProfilePicture)

                    userRepository.updateUser(userWithUrls)
                    _uiState.update {
                        it.copy(
                            user = userWithUrls,
                            isEditing = false,
                            isLoading = false,

                            validation = FormValidation()
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    validation = FormValidation(nError, sError, uError, rError, isFormValid),
                    isEditing = true
                )
            }
        }
    }

    fun addIngredient(item: String) {
        if (item.isNotBlank() && !_uiState.value.draftUser.favoriteIngredients.contains(item)) {
            _uiState.update {
                it.copy(
                    draftUser = it.draftUser.copy(
                        favoriteIngredients = it.draftUser.favoriteIngredients + item
                    )
                )
            }
        }
    }

    fun removeIngredient(item: String) {
        _uiState.update {
            it.copy(
                draftUser = it.draftUser.copy(
                    favoriteIngredients = it.draftUser.favoriteIngredients.filter { it != item }
                )
            )
        }
    }

    fun removeCuisine(item: String) {
        _uiState.update {
            it.copy(
                draftUser = it.draftUser.copy(
                    cuisineType = it.draftUser.cuisineType.filter { it != item }
                )
            )
        }
    }

    fun removeRestriction(item: String) {
        _uiState.update {
            it.copy(
                draftUser = it.draftUser.copy(
                    restriction = it.draftUser.restriction.filter { it != item }
                )
            )
        }
    }

    fun addCuisine(item: String) {
        if (item.isNotBlank() && !_uiState.value.draftUser.cuisineType.contains(item)) {
            _uiState.update {
                it.copy(
                    draftUser = it.draftUser.copy(
                        cuisineType = it.draftUser.cuisineType + item
                    )
                )
            }
        }
    }

    fun addRestriction(item: String) {
        if (item.isNotBlank() && !_uiState.value.draftUser.restriction.contains(item)) {
            _uiState.update {
                it.copy(
                    draftUser = it.draftUser.copy(
                        restriction = it.draftUser.restriction + item
                    )
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            userRepository: UserRepository,
            metadataRepository: MetadataRepository,
            ingredientRepository: IngredientRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(userRepository, metadataRepository, ingredientRepository) as T
            }
        }
    }
}

internal fun String.isValidEmail(): Boolean {
    if (this.isBlank())
        return false
    if (!this.contains('@') || !this.contains('.'))
        return false
    return true
}
