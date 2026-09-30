package com.example.myapplication.data

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class User(
    val id: String = Uuid.Companion.random().toString(),
    val userName: String = "",
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val cookingRole: String = "",
    val favoriteIngredients: List<String> = emptyList(),
    val profilePicture: String? = null,
    val cuisineType: List<String> = emptyList(),
    val restriction:List<String> = emptyList(),
    val userLists: Map<String, List<RecipeForCard>> = mapOf(
        "Executed" to emptyList(),
        "Favorite" to emptyList()
    ),
    val fcmTokens: List<String> = emptyList()  //list of tokens to allow firebase cloud messageing to send push notification to all the devices related to the user
)

fun User.toAuthor() = Author(
    id = id,
    name = name,
    surname = surname,
    profilePicture = profilePicture
)
