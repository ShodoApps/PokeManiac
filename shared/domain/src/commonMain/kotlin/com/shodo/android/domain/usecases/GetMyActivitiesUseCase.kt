//package com.shodo.android.domain.usecases
//
//import com.shodo.android.domain.repositories.entities.NewActivity
//import com.shodo.android.domain.repositories.myprofile.MyProfileRepository
//import kotlinx.coroutines.flow.Flow
//
//class GetMyActivitiesUseCase(private val myProfileRepository: MyProfileRepository) {
//
//    suspend fun execute(): Flow<List<NewActivity>> {
//        return myProfileRepository.getMyActivities()
//    }
//}