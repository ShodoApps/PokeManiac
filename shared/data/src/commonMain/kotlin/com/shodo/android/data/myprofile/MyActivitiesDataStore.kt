package com.shodo.android.data.myprofile

import com.shodo.android.domain.repositories.entities.NewActivity
import kotlinx.coroutines.flow.Flow

interface MyActivitiesDataStore {
    suspend fun saveNewActivity(newActivity: NewActivity)
    fun getAllActivities(): Flow<List<NewActivity>>
}