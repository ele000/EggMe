package com.example.myapplication.data

import android.content.Context
import androidx.credentials.CredentialManager
import com.example.myapplication.domain.IngredientRepository
import com.example.myapplication.domain.MetadataRepository
import com.example.myapplication.domain.NotificationRepository
import com.example.myapplication.domain.RecipeRepository
import com.example.myapplication.domain.UserRepository
import com.google.firebase.firestore.FirebaseFirestore


interface AppContainer {
    val recipeRepository: RecipeRepository
    val userRepository: UserRepository
    val ingredientRepository: IngredientRepository
    val metadataRepository: MetadataRepository
    val notificationRepository: NotificationRepository
}


class  DefaultAppContainer(private val context: Context) : AppContainer {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override val recipeRepository: RecipeRepository by lazy {
        FirebaseRecipeRepository(firestore)
    }

    override val userRepository: UserRepository by lazy {
        FirebaseUserRepository(firestore)
    }

    override val ingredientRepository: IngredientRepository by lazy {
        FirebaseIngredientRepository(firestore)
    }

    override val metadataRepository: MetadataRepository by lazy {
        FirebaseMetadataRepository(firestore)
    }

    override val notificationRepository: NotificationRepository by lazy {
        FirebaseNotificationRepository(firestore)
    }

}