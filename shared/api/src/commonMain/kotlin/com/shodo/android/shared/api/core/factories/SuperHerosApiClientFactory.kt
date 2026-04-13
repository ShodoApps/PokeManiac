package com.shodo.android.shared.api.core.factories

import com.shodo.android.shared.api.SUPER_HERO_API_TOKEN
import com.shodo.android.shared.api.core.remote.SuperHerosApiService
import com.shodo.android.shared.api.core.urlprovider.BaseUrlProvider
import io.ktor.client.HttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/** Mirrors the former Retrofit factory: pulls [BaseUrlProvider] from Koin and returns the typed HTTP facade. */
object SuperHerosApiClientFactory : KoinComponent {

    fun createService(httpClient: HttpClient): SuperHerosApiService {
        val baseUrlProvider: BaseUrlProvider = get()
        return SuperHerosApiService(httpClient, baseUrlProvider, SUPER_HERO_API_TOKEN)
    }
}
