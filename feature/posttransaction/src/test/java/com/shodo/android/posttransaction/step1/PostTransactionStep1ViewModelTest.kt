package com.shodo.android.posttransaction.step1

import android.content.Context
import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.File
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class PostTransactionStep1ViewModelTest {

    @Mock private lateinit var context: Context

    private lateinit var viewModel: PostTransactionStep1ViewModel

    // Real JVM temp directory — used to let File.createTempFile() succeed
    private val jvmTempDir = File(System.getProperty("java.io.tmpdir") ?: "/tmp")

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = PostTransactionStep1ViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ===== createImageFile() =====

    @Test
    fun `createImageFile returns a File when context filesDir is valid`() {
        // Given
        `when`(context.filesDir).thenReturn(jvmTempDir)

        // When
        val result = viewModel.createImageFile(context)

        // Then
        assertNotNull(result)
        assertTrue(result.name.startsWith("PokeManiac_"))
        assertTrue(result.name.endsWith(".jpg"))
        result.delete() // cleanup
    }

    @Test
    fun `createImageFile returns null and emits UiError when context filesDir throws`() = runTest {
        // Given
        `when`(context.filesDir).thenThrow(RuntimeException("No storage available"))

        // Subscriber must be set up BEFORE triggering (SharedFlow replay=0 drops events before subscription)
        viewModel.error.test {
            // When
            val result = viewModel.createImageFile(context)

            // Then
            assertNull(result)
            val error = awaitItem()
            assertTrue(error.message.isNotBlank())
        }
    }

    // ===== getUriForFile() =====

    @Test
    fun `getUriForFile returns null when file is null`() {
        // When
        val result = viewModel.getUriForFile(context, null)

        // Then — null file short-circuits before any Android call
        assertNull(result)
    }

    @Test
    fun `getUriForFile returns null and emits UiError when FileProvider throws`() = runTest {
        // Given — a real File in the temp directory
        `when`(context.filesDir).thenReturn(jvmTempDir)
        `when`(context.packageName).thenReturn("com.shodo.android")
        val tempFile = File.createTempFile("PokeManiac_test_", ".jpg", jvmTempDir)

        // FileProvider.getUriForFile() is an Android-only static method.
        // In JVM unit tests it throws RuntimeException("Stub!"), which the ViewModel catches,
        // emitting a UiError and returning null — this test verifies that error handling path.
        viewModel.error.test {
            // When
            val result = viewModel.getUriForFile(context, tempFile)

            // Then
            assertNull(result)
            assertTrue(awaitItem().message.isNotBlank())
        }

        tempFile.delete() // cleanup
    }
}
