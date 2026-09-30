package com.example.myapplication.data

import android.content.Context
import com.example.myapplication.domain.Collections
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.example.myapplication.domain.IngredientRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseIngredientRepository(
    private val firestore: FirebaseFirestore
) : IngredientRepository {

    private val ingredientsCollection = firestore.collection(Collections.INGREDIENTS)

    suspend fun initializeData(context: Context) {
        try {
            val existing = ingredientsCollection.limit(1).get().await()
            if (existing.isEmpty) {
                val jsonString =
                    context.assets.open("ingredients_seed.json").bufferedReader()
                        .use { it.readText() }
                val placeholderIngredients =
                    Gson().fromJson(jsonString, Array<IngredientOption>::class.java).toList()

                firestore.runBatch { batch ->
                    placeholderIngredients.forEach { ingredient ->
                        val docRef = ingredientsCollection.document(ingredient.name)
                        batch.set(docRef, ingredient)
                    }
                }.await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun getIngredients(): Flow<List<IngredientOption>> {
        return ingredientsCollection
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(IngredientOption::class.java)
            }
    }


}