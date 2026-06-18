package com.shodo.android.database.myactivities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "my_activities_table")
class MyActivityBase(
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "price") val price: Int,
    @ColumnInfo(name = "type") val type: NewActivityTypeBase,
    @ColumnInfo(name = "pokemonCard") val pokemonCard: MyPokemonCardBase
) {
    @Serializable
    data class MyPokemonCardBase(
        val id: Int,
        val name: String,
        val fileUri: String,
        val hasMyVote: Boolean,
        val totalVotes: Int
    )

    enum class NewActivityTypeBase { Purchase, Sale }
}
