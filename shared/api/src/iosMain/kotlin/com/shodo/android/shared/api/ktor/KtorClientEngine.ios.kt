package com.shodo.android.shared.api.ktor

import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.HttpClient as KtorClientEngine

internal actual fun getHttpEngine() = KtorClientEngine(Darwin) {
    installSuperHeroApiDefaults()
}
