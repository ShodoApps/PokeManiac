package com.shodo.android.database.friends

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "friends_table")
data class FriendBase(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "imageUrl") val imageUrl: String,
    @ColumnInfo(name = "isSubscribed") val isSubscribed: Boolean,
    @ColumnInfo(name = "pokemonCards") val pokemonCards: List<PokemonCardBase>,
) {
    @Serializable
    data class PokemonCardBase(
        val pokemonId: Int,
        val name: String,
        val imageUrl: String,
        val totalVotes: Int,
        val hasMyVote: Boolean,
    )
}
