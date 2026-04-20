package com.shodo.android.database.tracking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface TrackingEventScreenDao {

    @Insert(onConflict = REPLACE)
    suspend fun sendEvent(event: TrackingEventBase)

    @Query("SELECT * FROM tracking_event_table WHERE eventName = :eventName")
    suspend fun getEvent(eventName: String): TrackingEventBase?
}
