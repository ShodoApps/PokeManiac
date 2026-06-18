package com.shodo.android.shared.api.core.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** SuperHero API: success includes [resultFor] + [results]; error payloads often omit `results-for`. */
@Serializable
data class SearchCharacterDTO(
    val response: String = "",
    @SerialName("results-for")
    val resultFor: String = "",
    val results: List<SuperheroDTO> = emptyList(),
    val error: String? = null
)
