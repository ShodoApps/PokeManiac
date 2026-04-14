//
//package com.shodo.android.domain
//
//import com.shodo.android.domain.repositories.entities.User
//import com.shodo.android.domain.repositories.friends.UserRepository
//import com.shodo.android.domain.usecases.SearchFriendUseCase
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mock
//import org.mockito.Mockito.`when`
//import org.mockito.MockitoAnnotations
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.test.runTest
//import kotlin.test.assertEquals
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class SearchFriendUseCaseTest {
//
//    @Mock
//    private lateinit var userRepository: UserRepository
//
//    private lateinit var searchFriendUseCase: SearchFriendUseCase
//
//    @Before
//    fun setUp() {
//        MockitoAnnotations.openMocks(this)
//        searchFriendUseCase = SearchFriendUseCase(userRepository)
//    }
//
//    @Test
//    fun testGetSubscribedUsers() = runTest {
//        // GIVEN
//
//        `when`(userRepository.searchUser("friendName")).thenReturn(flow { emit(listOf(defaultUserSubscribed, defaultUserNotSubscribed2)) })
//
//        // WHEN
//        val result = searchFriendUseCase.execute("friendName").first()
//
//        // THEN
//        assertEquals(expected = listOf(defaultUserSubscribed, defaultUserNotSubscribed2), actual = result)
//    }
//
//    companion object {
//        private val defaultUserNotSubscribed2 = User(
//            id = "friendId2",
//            name = "friendName",
//            imageUrl = "friendImageUrl2",
//            description = "friendDescription2",
//            isSubscribed = false,
//            pokemonCards = listOf()
//        )
//        private val defaultUserSubscribed = User(
//            id = "friendId",
//            name = "friendName",
//            imageUrl = "friendImageUrl",
//            description = "friendDescription",
//            isSubscribed = true,
//            pokemonCards = listOf()
//        )
//    }
//}