package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.AppContainer
import com.example.myapplication.data.DefaultAppContainer
import com.example.myapplication.data.FirebaseIngredientRepository
import com.example.myapplication.data.FirebaseMetadataRepository
import com.example.myapplication.data.FirebaseNotificationRepository
import com.example.myapplication.data.FirebaseRecipeRepository
import com.example.myapplication.data.FirebaseUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

import com.example.myapplication.data.Recipe
import com.google.gson.Gson
import com.example.myapplication.data.Ingredient
import com.example.myapplication.data.Notification
import com.example.myapplication.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MyApplication : Application() {
    lateinit var container: AppContainer

    lateinit var auth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth

        container = DefaultAppContainer(context = applicationContext)


        MainScope().launch(Dispatchers.IO) {
            (container.recipeRepository as? FirebaseRecipeRepository)?.initializeData(context = applicationContext)
            (container.userRepository as? FirebaseUserRepository)?.initializeData(context = applicationContext)
            (container.ingredientRepository as? FirebaseIngredientRepository)?.initializeData(
                context = applicationContext
            )
            (container.metadataRepository as? FirebaseMetadataRepository)?.initializeData(context = applicationContext)
            (container.notificationRepository as? FirebaseNotificationRepository)?.initializeData(context = applicationContext)

        }

    }
}

