package com.shodo.android.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shodo.android.database.converters.Converters
import com.shodo.android.database.friends.FriendBase
import com.shodo.android.database.friends.FriendsDao
import com.shodo.android.database.myactivities.MyActivitiesDao
import com.shodo.android.database.myactivities.MyActivityBase
import com.shodo.android.database.tracking.TrackingEventBase
import com.shodo.android.database.tracking.TrackingEventClickDao
import com.shodo.android.database.tracking.TrackingEventScreenDao

@Database(entities = [FriendBase::class, TrackingEventBase::class, MyActivityBase::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PokeManiacDatabase : RoomDatabase() {

    abstract fun localFriendsDao(): FriendsDao

    abstract fun localMyActivitiesDao(): MyActivitiesDao

    abstract fun localScreenEventDao(): TrackingEventScreenDao

    abstract fun localClickEventDao(): TrackingEventClickDao
}
