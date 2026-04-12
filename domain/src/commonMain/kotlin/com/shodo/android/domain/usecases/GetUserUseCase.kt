//package com.shodo.android.domain.usecases
//
//import com.shodo.android.domain.repositories.entities.NoUserFoundException
//import com.shodo.android.domain.repositories.entities.User
//import com.shodo.android.domain.repositories.friends.UserRepository
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.map
//
//class GetUserUseCase(private val userRepository: UserRepository) {
//
//    fun execute(userId: String): Flow<User> {
//        return userRepository.getSubscribedUser(userId).map { it ?: throw NoUserFoundException(userId) }
//    }
//}