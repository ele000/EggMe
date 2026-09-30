package com.example.myapplication.data

import kotlin.uuid.Uuid

data class Author(
    val id: String = "",
    val name: String = "",
    val surname: String = "",
    val profilePicture: String? = null,
)
