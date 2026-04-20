package com.shodo.android.presentation

import kotlinx.coroutines.CoroutineDispatcher

/** Blocking-style repo / network work; [Dispatchers.IO] on Android/JVM, [Dispatchers.Default] on Native. */
internal expect val presentationIoDispatcher: CoroutineDispatcher
