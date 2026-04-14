package com.shodo.android.database.converters

import androidx.room.TypeConverter
import com.shodo.android.database.friends.FriendBase
import com.shodo.android.database.myactivities.MyActivityBase
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

class Converters {

    @TypeConverter
    fun fromPokemonCardList(value: List<FriendBase.PokemonCardBase>): String {
        return json.encodeToString(ListSerializer(FriendBase.PokemonCardBase.serializer()), value)
    }

    @TypeConverter
    fun toPokemonCardList(value: String): List<FriendBase.PokemonCardBase> {
        return json.decodeFromString(ListSerializer(FriendBase.PokemonCardBase.serializer()), value)
    }

    @TypeConverter
    fun fromMyPokemonCard(value: MyActivityBase.MyPokemonCardBase): String {
        return json.encodeToString(MyActivityBase.MyPokemonCardBase.serializer(), value)
    }

    @TypeConverter
    fun toMyPokemonCard(value: String): MyActivityBase.MyPokemonCardBase {
        return json.decodeFromString(MyActivityBase.MyPokemonCardBase.serializer(), value)
    }
}
