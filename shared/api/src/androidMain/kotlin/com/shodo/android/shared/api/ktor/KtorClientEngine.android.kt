package com.shodo.android.shared.api.ktor

import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.HttpClient as KtorClientEngine

internal actual fun getHttpEngine() = KtorClientEngine(OkHttp) {
    installSuperHeroApiDefaults()
}
