/**
 * Dashboard feed **presentation models**. Mapped from domain [NewActivity].
 * File image = opaque URI string (no Android [android.net.Uri] in shared code).
 */
package com.shodo.android.presentation.dashboard

import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.NewActivityType
import kotlinx.datetime.LocalDateTime

data class NewActivityUiModel(
    val id: String,
    val friendName: String,
    val friendImageUrl: String?,
    val date: String,
    val activityType: NewActivityTypeUiModel,
    val pokemonCard: PokemonCardUiModel,
    val price: Int,
)

enum class NewActivityTypeUiModel {
    Purchase,
    Sale,
}

data class PokemonCardUiModel(
    val name: String,
    val imageSource: DashboardImageSourceUiModel,
)

sealed class DashboardImageSourceUiModel {
    data class UrlSource(val imageUrl: String) : DashboardImageSourceUiModel()
    data class FileSource(val fileUri: String) : DashboardImageSourceUiModel()
}

fun NewActivity.mapToNewActivityUiModel(): NewActivityUiModel = NewActivityUiModel(
    id = userName + pokemonCard.name + date,
    friendName = userName,
    friendImageUrl = userImageUrl,
    date = date.formatDashboardDateTime(),
    activityType = activityType.mapToUiModel(),
    pokemonCard = PokemonCardUiModel(
        name = pokemonCard.name,
        imageSource = when (val source = pokemonCard.imageSource) {
            is ImageSource.UrlSource -> DashboardImageSourceUiModel.UrlSource(source.imageUrl)
            is ImageSource.FileSource -> DashboardImageSourceUiModel.FileSource(source.fileUri)
        },
    ),
    price = price,
)

private fun NewActivityType.mapToUiModel(): NewActivityTypeUiModel = when (this) {
    NewActivityType.Purchase -> NewActivityTypeUiModel.Purchase
    NewActivityType.Sale -> NewActivityTypeUiModel.Sale
}

/** Same pattern as former `dd/MM/yyyy HH:mm` without `java.time`. */
private fun LocalDateTime.formatDashboardDateTime(): String {
    fun Int.pad2(): String = toString().padStart(2, '0')
    val d = date
    val t = time
    return "${d.dayOfMonth.pad2()}/${d.monthNumber.pad2()}/${d.year} ${t.hour.pad2()}:${t.minute.pad2()}"
}
