package com.example.myapplication.data

import com.google.firebase.Timestamp
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Recipe(
    val id: String = Uuid.Companion.random().toString(),
    val title: String = "",
    val recipePicture: String="",
    val cost: Cost = Cost.CHEAP,
    val difficulty: Difficulty = Difficulty.EASY,
    val cuisineType: String = "",
    val time: Int = 0,
    val servings: Int = 0,
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<Step> = emptyList(),
    val dietaryRestrictions: List<String> = emptyList(),
    val author: Author = Author(),
    val rating: Double = 0.0,
    val reviews: List<Review> = emptyList(),
    val creationDate: Timestamp = Timestamp(0, 0),
    val forkedFromUserId: String? = "",
)


enum class Difficulty(val value: Int, val emoji: String) {
    EASY(1, "😄"),
    MEDIUM(2, "🙂"),
    DIFFICULT(3, "😓")
}

enum class Cost(val value: Int, val symbol: String) {
    CHEAP(1, "€"),
    MEDIUM(2, "€€"),
    EXPENSIVE(3, "€€€")
}

fun Recipe.toRecipeForCard() = RecipeForCard(
    id = this.id,
    title = this.title,
    recipePicture = this.recipePicture,
    time = this.time,
    rating = this.rating
)