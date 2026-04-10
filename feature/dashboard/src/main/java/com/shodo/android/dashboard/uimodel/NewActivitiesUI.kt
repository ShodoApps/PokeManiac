package com.shodo.android.dashboard.uimodel

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
data class NewActivityUI(
    val id: String,
    val friendName: String,
    val friendImageUrl: String?,
    val date: String,
    val activityType: NewActivityTypeUI,
    val pokemonCard: PokemonCardUI,
    val price: Int
)

@Immutable
enum class NewActivityTypeUI { Purchase, Sale }

@Stable
data class PokemonCardUI(
    val name: String,
    val imageSource: ImageSourceUI
)

@Stable
sealed class ImageSourceUI {
    @Immutable
    data class UrlSource(val imageUrl: String) : ImageSourceUI()
    @Stable
    data class FileSource(val fileUri: Uri) : ImageSourceUI()
}