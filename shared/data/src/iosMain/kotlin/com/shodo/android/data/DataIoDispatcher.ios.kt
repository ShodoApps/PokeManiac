package com.shodo.android.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val dataIoDispatcher: CoroutineDispatcher = Dispatchers.Default
