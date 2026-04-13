package com.shodo.android.shared.api.ktor

import io.ktor.client.HttpClient as KtorClientEngine

internal expect fun getHttpEngine(): KtorClientEngine
