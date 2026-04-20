package com.shodo.android.data

import kotlinx.coroutines.CoroutineDispatcher

/** Multiplatform substitute for [kotlinx.coroutines.Dispatchers.IO] (internal on Native). */
internal expect val dataIoDispatcher: CoroutineDispatcher
