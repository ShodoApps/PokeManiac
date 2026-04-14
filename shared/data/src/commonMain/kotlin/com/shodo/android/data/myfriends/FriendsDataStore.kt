package com.shodo.android.data.myfriends

import com.shodo.android.domain.repositories.entities.User
import kotlinx.coroutines.flow.Flow

interface FriendsDataStore {
    fun getFriendById(friendId: String): Flow<User?>
    suspend fun subscribeFriend(user: User)
    suspend fun unsubscribeFriend(userId: String)

    suspend fun updateFriend(user: User)

    fun getSubscribedFriends(): Flow<List<User>>
}