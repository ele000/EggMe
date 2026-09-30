package com.example.myapplication.domain

import com.example.myapplication.data.Author
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class GetAuthUserDataUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String?): Flow<Author?> {
        if (userId==null) {
            return flowOf(null)
        }
        return userRepository.getUser(userId).map { user ->
            user?.let {
                Author(
                    id = it.id,
                    name = it.name,
                    surname = it.surname,
                    profilePicture = it.profilePicture
                )
            }
        }
    }
}
