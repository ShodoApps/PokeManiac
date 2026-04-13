package com.shodo.android.shared.api.ktor

import io.ktor.client.HttpClient as KtorClientEngine
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun getHttpEngine() = KtorClientEngine(OkHttp) {
    installSuperHeroApiDefaults()
}
