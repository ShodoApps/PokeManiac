package com.shodo.android.shared.api.core.remote

import com.shodo.android.shared.api.core.remote.model.SearchCharacterDTO
import com.shodo.android.shared.api.core.urlprovider.BaseUrlProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodeURLPathPart
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

/**
 * Typed SuperHero HTTP API — same responsibility as the former Retrofit `SuperHerosApiService` interface.
 * Path shape matches the old `@GET("/api/{token}/search/{superHeroName}")` with [BaseUrlProvider] as base.
 */
class SuperHerosApiService(
    private val httpClient: HttpClient,
    private val baseUrlProvider: BaseUrlProvider,
    private val apiToken: String
) {

    suspend fun searchCharacter(superHeroName: String): SearchCharacterDTO {
        coroutineContext.ensureActive()
        val token = apiToken.encodeURLPathPart()
        val nameSegment = superHeroName.encodeURLPathPart()
        val base = baseUrlProvider.get().trimEnd('/')
        val url = "$base/$token/search/$nameSegment"
        val response = httpClient.get(url)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("HTTP ${response.status.value}: ${response.bodyAsText()}")
        }
        return response.body()
    }
}
