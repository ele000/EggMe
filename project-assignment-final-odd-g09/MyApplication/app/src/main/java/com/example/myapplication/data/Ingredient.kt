package com.example.myapplication.data

data class Ingredient(
    val name: String = "",
    val quantity: Int = 0,
    val unityOfMeasurement: String = "pcs"
)

data class IngredientOption(
    val name: String = "",
    val unityOfMeasurement: String = "pcs"
)