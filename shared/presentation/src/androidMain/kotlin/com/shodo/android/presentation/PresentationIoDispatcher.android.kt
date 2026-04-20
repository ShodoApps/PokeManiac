package com.shodo.android.presentation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val presentationIoDispatcher: CoroutineDispatcher = Dispatchers.IO
