package com.shodo.android.domain.repositories.friends

import com.shodo.android.domain.repositories.entities.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getSubscribedUsers(): Flow<List<User>>
    fun getSubscribedUser(userId: String): Flow<User?>

    fun searchUsers(userName: String): Flow<List<User>>

    suspend fun subscribeUser(user: User)
    suspend fun unsubscribeUser(userId: String)
}
