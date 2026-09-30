package com.example.myapplication.data

import com.google.firebase.Timestamp
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Review(
    val id: String = Uuid.Companion.random().toString(),
    val description: String = "",
    val rating: Int = 0,
    val photo: String = "",
    val reviewDate: Timestamp = Timestamp(0, 0),
    val author: Author = Author()
)
