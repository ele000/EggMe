package com.example.myapplication.domain

import com.example.myapplication.data.Ingredient
import com.example.myapplication.data.IngredientOption
import kotlinx.coroutines.flow.Flow

interface IngredientRepository {

    fun getIngredients(): Flow<List<IngredientOption>>
}