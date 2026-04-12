package com.shodo.android.domain.repositories.myprofile

import com.shodo.android.domain.repositories.entities.NewActivity
import kotlinx.coroutines.flow.Flow

interface MyProfileRepository {
    fun getMyActivities(): Flow<List<NewActivity>>
}