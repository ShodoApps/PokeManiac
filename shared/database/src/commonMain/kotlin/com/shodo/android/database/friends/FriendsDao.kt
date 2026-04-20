package com.shodo.android.database.friends

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendsDao {

    @Query("SELECT * FROM friends_table")
    fun getAllFriends(): Flow<List<FriendBase>>

    @Query("SELECT * FROM friends_table WHERE id = :friendId")
    fun getFriendById(friendId: String): Flow<FriendBase?>

    @Insert(onConflict = REPLACE)
    suspend fun insert(character: FriendBase)

    @Query("DELETE FROM friends_table WHERE id = :friendId")
    suspend fun deleteFriend(friendId: String)
}
