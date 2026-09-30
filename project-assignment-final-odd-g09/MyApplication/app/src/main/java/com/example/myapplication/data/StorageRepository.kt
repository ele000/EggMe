package com.example.myapplication.data

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

object StorageRepository {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference.child("eggme_photos")

    suspend fun uploadImage(imageUri: Uri): String {
        val fileName = "${UUID.randomUUID()}.jpg"
        val fileRef = storageRef.child(fileName)

        fileRef.putFile(imageUri).await()

        return fileRef.downloadUrl.await().toString()
    }

    suspend fun getDownloadUrl(path: String): String {
        return storage.getReference(path).downloadUrl.await().toString()
    }


    suspend fun deleteImage(imageUrl: String) {
        try {
            if (imageUrl.startsWith("https://firebasestorage.googleapis.com/")) {
                val fileRef = storage.getReferenceFromUrl(imageUrl)
                fileRef.delete().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}