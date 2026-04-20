package com.shodo.android.data.myprofile

import com.shodo.android.domain.datastore.MyActivitiesDataStore
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.myprofile.MyProfileRepository
import kotlinx.coroutines.flow.Flow

class MyProfileRepositoryImpl(private val myActivitiesDataStore: MyActivitiesDataStore) : MyProfileRepository {
    override fun getMyActivities(): Flow<List<NewActivity>> {
        return myActivitiesDataStore.getAllActivities()
    }
}