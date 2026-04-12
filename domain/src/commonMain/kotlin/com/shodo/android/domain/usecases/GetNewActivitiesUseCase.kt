//package com.shodo.android.domain.usecases
//
//import com.shodo.android.domain.repositories.entities.NewActivity
//import com.shodo.android.domain.repositories.news.NewsFeedRepository
//import kotlinx.coroutines.flow.Flow
//
//class GetNewActivitiesUseCase(private val newsFeedRepository: NewsFeedRepository) {
//
//    suspend fun execute(): Flow<List<NewActivity>> {
//        return newsFeedRepository.getNewActivities()
//    }
//}