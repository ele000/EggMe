package com.example.myapplication.domain

import com.example.myapplication.data.Recipe
import com.example.myapplication.domain.UserRepository
import com.example.myapplication.domain.RecipeRepository

class SaveRecipeInUserListUseCase (
    private val userRepository: UserRepository
){
    suspend operator fun invoke(userId: String, title: String, recipe: Recipe
    ): Boolean{
        return userRepository.addRecipeToUserList(userId, title, recipe)
    }
}
