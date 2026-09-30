package com.example.myapplication.data

import android.content.Context
import com.example.myapplication.domain.Collections
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.example.myapplication.domain.RecipeRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseRecipeRepository(
    private val firestore: FirebaseFirestore
)  : RecipeRepository {

    private val recipesCollection = firestore.collection(Collections.RECIPES)

    suspend fun initializeData(context: Context) {
        try {
            val existing = recipesCollection.limit(1).get().await()
            if (existing.isEmpty) {
                val jsonString =
                    context.assets.open("seed_data.json").bufferedReader().use { it.readText() }
                val placeholderRecipes = Gson().fromJson(jsonString, Array<Recipe>::class.java).toList()

                val recipesWithUrls = mutableListOf<Recipe>()
                
                for (recipe in placeholderRecipes) {
                    val urlCover = try { StorageRepository.getDownloadUrl(recipe.recipePicture) } catch (e: Exception) { "" }

                    val urlSteps = mutableListOf<Step>()
                    for (step in recipe.steps) {
                        val urlStepPic = try { StorageRepository.getDownloadUrl(step.picture) } catch (e: Exception) { "" }
                        urlSteps.add(step.copy(picture = urlStepPic))
                    }

                    val urlReviews = mutableListOf<Review>()
                    for (review in recipe.reviews) {
                        val urlReviewPic = try { StorageRepository.getDownloadUrl(review.photo) } catch (e: Exception) { "" }
                        urlReviews.add(review.copy(photo = urlReviewPic))
                    }

                    recipesWithUrls.add(recipe.copy(recipePicture = urlCover, steps = urlSteps, reviews = urlReviews))
                }

                firestore.runBatch { batch ->
                    recipesWithUrls.forEach { recipe ->
                        val docRef = recipesCollection.document(recipe.id)
                        batch.set(docRef, recipe)
                    }
                }.await()
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getRecipes(): Flow<List<Recipe>> {
        return recipesCollection
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Recipe::class.java)
            }
    }

    override fun getRecipe(id: String): Flow<Recipe?> {
        return recipesCollection.document(id)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(Recipe::class.java)
            }
    }

    override fun getRecipesByOwner(ownerId: String): Flow<List<Recipe>> {
        return recipesCollection
            .whereEqualTo("author.id", ownerId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Recipe::class.java)
            }
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        try {
            recipesCollection.document(recipe.id)
                .set(recipe)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun addRecipe(recipe: Recipe): String {
        return try {

            val newDocumentRef = recipesCollection.document()

            val generatedId = newDocumentRef.id

            val recipeWithId = recipe.copy(id = generatedId)

            newDocumentRef.set(recipeWithId).await()

            generatedId
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteRecipe(id: String) {
        try {
            recipesCollection.document(id)
                .delete()
                .await()
        } catch (e: Exception) {
            throw e
        }
    }
}