package com.shodo.android.database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

private const val DATABASE_FILE_NAME = "PokeManiacDB"

fun buildPokeManiacDatabase(context: Context): PokeManiacDatabase {
    val dbFile = context.getDatabasePath(DATABASE_FILE_NAME)
    return Room.databaseBuilder<PokeManiacDatabase>(
        context = context,
        name = dbFile.absolutePath
    ).setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(false)
        .build()
}
