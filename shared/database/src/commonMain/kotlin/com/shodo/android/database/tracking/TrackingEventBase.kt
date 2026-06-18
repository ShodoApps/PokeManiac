package com.shodo.android.database.tracking

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracking_event_table")
data class TrackingEventBase(
    @PrimaryKey
    @ColumnInfo(name = "eventName")
    val name: String,
    @ColumnInfo(name = "eventCount") val count: Int
)
