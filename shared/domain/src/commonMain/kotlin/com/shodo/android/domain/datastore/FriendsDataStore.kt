package com.shodo.android.domain.datastore

import com.shodo.android.domain.repositories.entities.User
import kotlinx.coroutines.flow.Flow

/** Local persistence for subscribed friends; implemented by Room in `:shared:database`. */
interface FriendsDataStore {
    fun getFriendById(friendId: String): Flow<User?>
    suspend fun subscribeFriend(user: User)
    suspend fun unsubscribeFriend(userId: String)

    suspend fun updateFriend(user: User)

    fun getSubscribedFriends(): Flow<List<User>>
}
