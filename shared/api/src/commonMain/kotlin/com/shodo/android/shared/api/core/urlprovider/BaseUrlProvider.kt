package com.shodo.android.shared.api.core.urlprovider

/** Same role as the former `api` module: base URL for SuperHero API requests. */
class BaseUrlProvider {
    fun get(): String = "https://superheroapi.com/api/"
}
