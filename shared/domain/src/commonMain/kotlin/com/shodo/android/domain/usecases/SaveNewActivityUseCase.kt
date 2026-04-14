//package com.shodo.android.domain.usecases
//
//import com.shodo.android.domain.repositories.entities.NewActivity
//import com.shodo.android.domain.repositories.news.NewsFeedRepository
//
//class SaveNewActivityUseCase(private val newsFeedRepository: NewsFeedRepository) {
//    suspend fun execute(newActivity: NewActivity) {
//        newsFeedRepository.saveNewActivity(newActivity)
//    }
//}