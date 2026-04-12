//package com.shodo.android.domain.usecases
//
//import com.shodo.android.domain.repositories.friends.UserRepository
//import com.shodo.android.domain.repositories.entities.User
//import kotlinx.coroutines.flow.Flow
//
//class SearchFriendUseCase(private val userRepository: UserRepository) {
//
//    suspend fun execute(name: String): Flow<List<User>> {
//        return userRepository.searchUser(name)
//    }
//}