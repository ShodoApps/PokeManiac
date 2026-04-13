package com.shodo.android.shared.api.core.factories

import com.shodo.android.shared.api.ktor.getHttpEngine
import io.ktor.client.HttpClient

object HttpClientFactory {

    fun create(): HttpClient = getHttpEngine()
}
