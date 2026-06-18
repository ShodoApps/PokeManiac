package com.shodo.android.data

import app.cash.turbine.test
import com.shodo.android.data.myprofile.MyProfileRepositoryImpl
import com.shodo.android.domain.datastore.MyActivitiesDataStore
import com.shodo.android.domain.repositories.entities.ImageSource
import com.shodo.android.domain.repositories.entities.NewActivity
import com.shodo.android.domain.repositories.entities.NewActivityType
import com.shodo.android.domain.repositories.entities.UserPokemonCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class MyProfileRepositoryImplTest {

    @Mock private lateinit var myActivitiesDataStore: MyActivitiesDataStore

    private lateinit var repository: MyProfileRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = MyProfileRepositoryImpl(myActivitiesDataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getMyActivities delegates to dataStore`() = runTest {
        // Given
        val expected = listOf(defaultActivity)
        `when`(myActivitiesDataStore.getAllActivities()).thenReturn(flow { emit(expected) })

        // When
        repository.getMyActivities().test {
            // Then
            assertEquals(expected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    companion object {
        private val defaultActivity = NewActivity(
            userName = "Ash",
            userImageUrl = null,
            date = LocalDateTime(2024, 6, 1, 12, 0),
            pokemonCard = UserPokemonCard(
                pokemonId = 25,
                name = "Pikachu",
                imageSource = ImageSource.UrlSource("https://image.url/pikachu.jpg"),
                totalVotes = 5,
                hasMyVote = true
            ),
            activityType = NewActivityType.Purchase,
            price = 30
        )
    }
}
