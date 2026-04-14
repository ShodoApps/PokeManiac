package com.shodo.android.domain.repositories.friends

import com.shodo.android.domain.repositories.entities.User

/** Remote friends search; implemented in `:shared:api` (Ktor). */
interface FriendsRequest {
    suspend fun searchUsers(friendName: String): List<User>
}
