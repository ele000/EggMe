package com.example.myapplication.ui.navigation


import kotlinx.serialization.Serializable

@Serializable
data class ReviewListRoute(val recipeId: String)

@Serializable
data class CreateReviewRoute(val recipeId: String)

@Serializable
object HomeRoute

@Serializable
object RecipesListRoute

@Serializable
object CreateRecipeRoute

@Serializable
object EggRoute

@Serializable
object MyRecipesRoute

@Serializable
object ProfileRoute

@Serializable
object EditProfileRoute

@Serializable
object CreateProfileRoute
@Serializable
data class RecipeDetailRoute(val recipeId: String)

@Serializable
object ProfileImageRoute
@Serializable
object RecipeCoverImageRoute
@Serializable
data class RecipeStepImageRoute(val stepIndex: Int)
@Serializable
object RecipeReviewImageRoute
@Serializable
object DeleteConfirmationRoute

@Serializable
data class RecipeCreationTimeRoute(val initialMinutes: Int)
@Serializable
data class RecipeFilterTimeRoute(val initialMinutes: Int)

@Serializable
object FiltersRoute

@Serializable
data class ProfileIngredientsRoute(val suggestions: List<String>, val selected: List<String>)
@Serializable
data class ProfileCuisinesRoute(val suggestions: List<String>, val selected: List<String>)
@Serializable
data class ProfileRestrictionsRoute(val suggestions: List<String>, val selected: List<String>)

@Serializable
data class FilterIngredientsRoute(val suggestions: List<String>, val selected: List<String>)
@Serializable
data class FilterCuisinesRoute(val suggestions: List<String>, val selected: List<String>)
@Serializable object AuthGraph
@Serializable object LoginRoute
@Serializable object RegisterRoute
@Serializable object MainGraph

@Serializable
object NotificationsRoute

@Serializable
object FindYourFarmersRoute

