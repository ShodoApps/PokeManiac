@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.shodo.android.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

private const val DATABASE_FILE_NAME = "PokeManiacDB"

fun buildPokeManiacDatabase(): PokeManiacDatabase {
    val documentDirectory =
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
    val dirPath = requireNotNull(documentDirectory?.path)
    val dbFilePath = "$dirPath/$DATABASE_FILE_NAME"
    return Room.databaseBuilder<PokeManiacDatabase>(
        name = dbFilePath,
    ).setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(false)
        .build()
}

private val iosSharedDatabase: PokeManiacDatabase by lazy { buildPokeManiacDatabase() }

/** Single Room instance for the iOS process (matches Android Koin `single { }`). */
fun sharedPokeManiacDatabase(): PokeManiacDatabase = iosSharedDatabase
