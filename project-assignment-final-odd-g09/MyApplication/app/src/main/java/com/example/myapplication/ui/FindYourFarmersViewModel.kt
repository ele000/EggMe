package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.Farmer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FindYourFarmersViewModel : ViewModel() {
    private val _farmersList = MutableStateFlow<List<Farmer>>(emptyList())
    val farmersList: StateFlow<List<Farmer>> = _farmersList.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showResults = MutableStateFlow(false)
    val showResults: StateFlow<Boolean> = _showResults.asStateFlow()

    private val _filteredFarmers = MutableStateFlow<List<Farmer>>(emptyList())
    val filteredFarmers: StateFlow<List<Farmer>> = _filteredFarmers.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.update { query }
        _showResults.value = query.isNotEmpty()

        _filteredFarmers.value = if (query.isBlank()) {
            _farmersList.value
        } else {
            _farmersList.value.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    fun hideResults() {
        _showResults.value = false
    }

    fun setFarmers(farmers: List<Farmer>) {
        _farmersList.value = farmers
        _filteredFarmers.value = farmers
    }

    companion object {
        fun provideFactory(
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FindYourFarmersViewModel() as T
            }
        }

    }
}