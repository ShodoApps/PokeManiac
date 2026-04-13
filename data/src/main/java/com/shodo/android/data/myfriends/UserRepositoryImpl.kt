package com.shodo.android.data.myfriends

import com.shodo.android.domain.repositories.entities.User
import com.shodo.android.domain.repositories.friends.FriendsRequest
import com.shodo.android.domain.repositories.friends.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserRepositoryImpl(
    private val friendsRequest: FriendsRequest,
    private val friendsDataStore: FriendsDataStore
) : UserRepository {

    override fun getSubscribedUsers(): Flow<List<User>> {
        return friendsDataStore.getSubscribedFriends()
    }

    override fun getSubscribedUser(userId: String): Flow<User?> {
        return friendsDataStore.getFriendById(userId)
    }

    override fun searchUsers(userName: String): Flow<List<User>> {
        val subscribedFriends = friendsDataStore.getSubscribedFriends()
        val searchFriends = flow { emit(friendsRequest.searchUsers(userName)) }
        return combine(searchFriends, subscribedFriends) { searchResults, subscribedUsers ->
            val subscribedIds = subscribedUsers.map { it.id }.toSet()
            searchResults.map { searchResult ->
                searchResult.copy(isSubscribed = searchResult.id in subscribedIds)
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun subscribeUser(user: User) {
        friendsDataStore.subscribeFriend(user.copy(isSubscribed = true))
    }

    override suspend fun unsubscribeUser(userId: String) {
        return friendsDataStore.unsubscribeFriend(userId)
    }
}