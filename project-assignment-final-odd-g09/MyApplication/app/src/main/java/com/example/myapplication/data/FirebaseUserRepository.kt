package com.example.myapplication.data

import android.content.Context
import com.example.myapplication.domain.Collections
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.example.myapplication.domain.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseUserRepository(
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val usersCollection = firestore.collection(Collections.USERS)

    suspend fun initializeData(context: Context) {
        try {
            val existing = usersCollection.limit(1).get().await()
            if (existing.isEmpty) {
                val jsonString =
                    context.assets.open("users_seed.json").bufferedReader().use { it.readText() }
                val placeholderUsers = Gson().fromJson(jsonString, Array<User>::class.java).toList()

                val usersWithUrls = mutableListOf<User>()
                for (user in placeholderUsers) {
                    val normalizedUserLists = user.userLists.mapValues { list ->
                        list.value.map { recipe ->
                            val urlCover = try {
                                StorageRepository.getDownloadUrl(recipe.recipePicture)
                            } catch (e: Exception) {
                                ""
                            }

                            recipe.copy(
                                recipePicture = urlCover
                            )
                        }
                    }

                    usersWithUrls.add(user.copy(userLists = normalizedUserLists))
                }
                firestore.runBatch { batch ->
                    usersWithUrls.forEach { user ->
                        val docRef = usersCollection.document(user.id)
                        batch.set(docRef, user)
                    }
                }.await()
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getUser(userId: String): Flow<User?> {
        return usersCollection.document(userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(User::class.java)
            }
    }

    override fun getUserLists(userId: String): Flow<Map<String, List<RecipeForCard>>?> {
        return usersCollection.document(userId)
            .snapshots()
            .map { snapshot ->
                val user = snapshot.toObject(User::class.java)
                user?.userLists
            }
    }

    override suspend fun updateUser(user: User) {
        try {
            usersCollection.document(user.id)
                .set(user)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun addList(userId: String, title: String): Boolean {
        return try {
            usersCollection.document(userId)
                .update("userLists.$title", emptyList<RecipeForCard>())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun removeList(userId: String, title: String): Boolean {
        return try {
            usersCollection.document(userId)
                .update("userLists.$title", FieldValue.delete())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun addRecipeToUserList(
        userId: String,
        title: String,
        recipe: Recipe
    ): Boolean {
        return try {
            val recipeCard=recipe.toRecipeForCard()
            usersCollection.document(userId)
                .update("userLists.$title", FieldValue.arrayUnion(recipeCard))
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun removeRecipeFromUserList(
        userId: String,
        title: String,
        recipeId: String
    ): Boolean {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            val user = snapshot.toObject(User::class.java)
            val currentList = user?.userLists?.get(title) ?: emptyList()

            val recipeToRemove = currentList.find { it.id == recipeId }

            if (recipeToRemove != null) {
                usersCollection.document(userId)
                    .update("userLists.$title", FieldValue.arrayRemove(recipeToRemove))
                    .await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun registerUserIfNotPresent(newUser: User) : Boolean{
        try {
            val doc = usersCollection.document(newUser.id)
            val user = doc.get().await()

            if (!user.exists()) {
                doc.set(newUser).await()
                return false
            } else {
                return true
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun addFcmToken(userId: String, token: String) {
        try {
            usersCollection.document(userId)
                .update("fcmTokens", FieldValue.arrayUnion(token))
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun removeFcmToken(userId: String, token: String) {
        try {
            usersCollection.document(userId)
                .update("fcmTokens", FieldValue.arrayRemove(token))
                .await()
        } catch (e: Exception) {
            throw e
        }
    }
}