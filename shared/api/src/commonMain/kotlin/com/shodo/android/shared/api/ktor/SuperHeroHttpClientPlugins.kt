package com.shodo.android.shared.api.ktor

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TWO_MINUTES_MS = 120_000L

/** Shared Ktor plugins for SuperHero API calls (used from each platform’s `HttpClient(Factory) { … }`). */
internal fun HttpClientConfig<*>.installSuperHeroApiDefaults() {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        )
    }
    install(HttpTimeout) {
        requestTimeoutMillis = TWO_MINUTES_MS
        connectTimeoutMillis = TWO_MINUTES_MS
        socketTimeoutMillis = TWO_MINUTES_MS
    }
}
