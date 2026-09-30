package com.example.myapplication.data

import android.content.Context
import com.example.myapplication.domain.Collections
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.example.myapplication.domain.MetadataRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseMetadataRepository(
    private val firestore: FirebaseFirestore
) : MetadataRepository {

    private val metadataCollection = firestore.collection(Collections.METADATA)

    suspend fun initializeData(context: Context) {
        try {
            val cuisinesRef = metadataCollection.document("cuisines")
            val cuisinesDoc = cuisinesRef.get().await()

            if (!cuisinesDoc.exists() || cuisinesDoc.get("list") == null) {
                val jsonString = context.assets.open("cuisines_seed.json").bufferedReader().use { it.readText() }
                val cuisines = Gson().fromJson(jsonString, Array<String>::class.java).toList()
                cuisinesRef.set(mapOf("list" to cuisines)).await()
            }

            val restrictionsRef = metadataCollection.document("restrictions")
            val restrictionsDoc = restrictionsRef.get().await()

            if (!restrictionsDoc.exists() || restrictionsDoc.get("list") == null) {
                val jsonString = context.assets.open("restrictions_seed.json").bufferedReader().use { it.readText() }
                val restrictions = Gson().fromJson(jsonString, Array<String>::class.java).toList()

                restrictionsRef.set(mapOf("list" to restrictions)).await()
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun getCuisines(): Flow<List<String>> {
        return metadataCollection.document("cuisines")
            .snapshots()
            .map { snapshot ->
                @Suppress("UNCHECKED_CAST")
                val list = snapshot.get("list") as? List<String>
                list ?: emptyList()
            }    }

    override fun getDietaryRestrictions(): Flow<List<String>> {
        return metadataCollection.document("restrictions")
            .snapshots()
            .map { snapshot ->
                @Suppress("UNCHECKED_CAST")
                val list = snapshot.get("list") as? List<String>

                list ?: emptyList()
            }    }
}