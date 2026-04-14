package com.shodo.android.database.myactivities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MyActivitiesDao {

    @Query("SELECT * FROM my_activities_table")
    fun getAllActivities(): Flow<List<MyActivityBase>>

    @Insert(onConflict = REPLACE)
    fun insert(myActivity: MyActivityBase)
}
