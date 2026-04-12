//package com.shodo.android.domain.usecases
//
//import com.shodo.android.domain.repositories.entities.User
//import com.shodo.android.domain.repositories.friends.UserRepository
//import kotlinx.coroutines.flow.Flow
//
//class GetMyFriendsUseCase(private val userRepository: UserRepository) {
//
//    fun execute(): Flow<List<User>> {
//        return userRepository.getSubscribedUsers()
//    }
//}