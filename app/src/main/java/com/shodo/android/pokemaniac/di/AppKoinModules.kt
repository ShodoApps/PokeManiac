package com.shodo.android.pokemaniac.di

import com.shodo.android.shared.di.sharedKoinArchiModules
import org.koin.core.module.Module

/**
 * Core data-layer Koin modules for `startKoin`: Room + DataStores first, then `commonMain` API/repository wiring from `:shared:di`.
 */
fun appCoreArchiModules(): List<Module> = listOf(databaseModule) + sharedKoinArchiModules
