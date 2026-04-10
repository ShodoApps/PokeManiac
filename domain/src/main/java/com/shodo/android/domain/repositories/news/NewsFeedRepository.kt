package com.shodo.android.domain.repositories.news

import com.shodo.android.domain.repositories.entities.NewActivity
import kotlinx.coroutines.flow.Flow

interface NewsFeedRepository {
    fun getNewActivities(): Flow<List<NewActivity>>
    suspend fun saveNewActivity(newActivity: NewActivity)
}