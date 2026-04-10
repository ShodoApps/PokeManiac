package com.shodo.android.dashboard.uimodel

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.NewActivityType
import java.time.format.DateTimeFormatter

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

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

fun NewActivity.mapToUI(): NewActivityUI = NewActivityUI(
    id = userName + pokemonCard.name + date,
    friendName = userName,
    friendImageUrl = userImageUrl,
    date = date.format(DATE_FORMATTER),
    activityType = activityType.mapToUI(),
    pokemonCard = PokemonCardUI(
        name = pokemonCard.name,
        imageSource = when (val source = pokemonCard.imageSource) {
            is ImageSource.UrlSource -> ImageSourceUI.UrlSource(source.imageUrl)
            is ImageSource.FileSource -> ImageSourceUI.FileSource(source.fileUri.toUri())
        }
    ),
    price = price
)

private fun NewActivityType.mapToUI() = when (this) {
    NewActivityType.Purchase -> NewActivityTypeUI.Purchase
    NewActivityType.Sale -> NewActivityTypeUI.Sale
}