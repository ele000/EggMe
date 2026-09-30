package com.example.myapplication.domain

import com.example.myapplication.data.Recipe

data class RecipeWithSaved(
    val recipe: Recipe,
    val isSaved: Boolean
)