package com.example.myapplication.domain

import kotlinx.coroutines.flow.Flow

interface MetadataRepository {
    fun getCuisines(): Flow<List<String>>
    fun getDietaryRestrictions(): Flow<List<String>>
}
