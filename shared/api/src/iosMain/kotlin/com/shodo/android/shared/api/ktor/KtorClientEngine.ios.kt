package com.shodo.android.shared.api.ktor

import io.ktor.client.HttpClient as KtorClientEngine
import io.ktor.client.engine.darwin.Darwin

internal actual fun getHttpEngine() = KtorClientEngine(Darwin) {
    installSuperHeroApiDefaults()
}
