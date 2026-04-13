package com.shodo.android.shared.api.core.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuperheroDTO(
    val id: String,
    val name: String,
    val powerstats: PowerStatsDTO,
    val biography: BiographyDTO,
    val appearance: AppearanceDTO,
    val work: WorkDTO,
    val connections: ConnectionsDTO,
    val image: ImageDTO,
)

@Serializable
data class PowerStatsDTO(
    val intelligence: String,
    val strength: String,
    val speed: String,
    val durability: String,
    val power: String,
    val combat: String,
)

@Serializable
data class BiographyDTO(
    @SerialName("full-name")
    val fullName: String,
    @SerialName("alter-egos")
    val alterEgos: String,
    val aliases: List<String> = emptyList(),
    @SerialName("place-of-birth")
    val placeOfBirth: String,
    @SerialName("first-appearance")
    val firstAppearance: String,
    val publisher: String,
    val alignment: String,
)

@Serializable
data class AppearanceDTO(
    val gender: String,
    val race: String,
    val height: List<String> = emptyList(),
    val weight: List<String> = emptyList(),
    @SerialName("eye-color")
    val eyeColor: String,
    @SerialName("hair-color")
    val hairColor: String,
)

@Serializable
data class WorkDTO(
    val occupation: String,
    val base: String,
)

@Serializable
data class ConnectionsDTO(
    @SerialName("group-affiliation")
    val groupAffiliation: String,
    val relatives: String,
)

@Serializable
data class ImageDTO(
    val url: String,
)
