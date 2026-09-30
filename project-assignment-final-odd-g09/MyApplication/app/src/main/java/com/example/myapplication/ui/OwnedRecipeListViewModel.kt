package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.auth.SessionManagerFacade
import com.example.myapplication.data.Recipe
import com.example.myapplication.data.RecipeForCard
import com.example.myapplication.data.User
import com.example.myapplication.domain.GetUserListsUseCase
import com.example.myapplication.domain.IngredientRepository
import com.example.myapplication.domain.MetadataRepository
import com.example.myapplication.domain.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class OwnedRecipeListViewModel(
    private val userRepository: UserRepository,
    private val getUserListsUseCase: GetUserListsUseCase
) : ViewModel() {

    private val _userLists = MutableStateFlow<Map<String, List<RecipeForCard>>>(emptyMap())
    val userLists: StateFlow<Map<String, List<RecipeForCard>>> = _userLists.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)

    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    private var listsJob: Job? = null
    private var userJob:Job? = null
    private var authJob:Job? = null


    init {
        authJob = viewModelScope.launch {
            SessionManagerFacade.currentUserStateFlow.collect { userId ->
                listsJob?.cancel()
                userJob?.cancel()

                if (userId == null) {
                    _userLists.value = emptyMap()
                    _currentUser.value = null
                } else {
                    listsJob = viewModelScope.launch {
                        getUserListsUseCase(userId).collect { map ->
                            _userLists.update { map }
                        }
                    }
                    userJob = viewModelScope.launch {
                        userRepository.getUser(userId).collect { user ->
                            _currentUser.update { user }
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        listsJob?.cancel()
        super.onCleared()
    }





    companion object {
        fun provideFactory(
            userRepository: UserRepository,
            getUserListsUseCase: GetUserListsUseCase
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OwnedRecipeListViewModel(userRepository, getUserListsUseCase) as T
            }
        }

    }
}
