package com.shodo.android.posttransaction.step1

import app.cash.turbine.test
import com.shodo.android.presentation.posttransaction.PostTransactionStep1ImageCapturePort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class PostTransactionStep1ViewModelTest {

    @Mock
    private lateinit var imageCapture: PostTransactionStep1ImageCapturePort

    private lateinit var viewModel: PostTransactionStep1ViewModel

    private val jvmTempDir = File(System.getProperty("java.io.tmpdir") ?: "/tmp")

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = PostTransactionStep1ViewModel(imageCapture)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `createImageFile returns File when port succeeds`() {
        val path = File(jvmTempDir, "PokeManiac_ok_${System.nanoTime()}.jpg").apply { createNewFile() }.absolutePath
        `when`(imageCapture.createTempJpegFileAbsolutePath()).thenReturn(Result.success(path))

        val result = viewModel.createImageFile()

        assertNotNull(result)
        assertEquals(path, result.absolutePath)
        result.delete()
    }

    @Test
    fun `createImageFile returns null and emits UiError when port fails`() = runTest {
        val failure = RuntimeException("No storage")
        `when`(imageCapture.createTempJpegFileAbsolutePath()).thenReturn(Result.failure(failure))

        viewModel.error.test {
            val result = viewModel.createImageFile()
            assertNull(result)
            val error = awaitItem()
            assertTrue(error.message.isNotBlank())
        }
    }

    @Test
    fun `getUriForFile returns null when file is null`() {
        assertNull(viewModel.getUriForFile(null))
    }

    @Test
    fun `getUriForFile returns Uri when port succeeds`() {
        val tempFile = File.createTempFile("PokeManiac_test_", ".jpg", jvmTempDir)
        val uriString = "content://com.example/posttransaction/photo.jpg"
        `when`(imageCapture.contentUriStringForAbsolutePath(tempFile.absolutePath))
            .thenReturn(Result.success(uriString))

        val result = viewModel.getUriForFile(tempFile)

        assertNotNull(result)
        assertEquals(uriString, result.toString())
        tempFile.delete()
    }

    @Test
    fun `getUriForFile returns null and emits UiError when port fails`() = runTest {
        val tempFile = File.createTempFile("PokeManiac_test_", ".jpg", jvmTempDir)
        `when`(imageCapture.contentUriStringForAbsolutePath(tempFile.absolutePath))
            .thenReturn(Result.failure(RuntimeException("Provider missing")))

        viewModel.error.test {
            val result = viewModel.getUriForFile(tempFile)
            assertNull(result)
            assertTrue(awaitItem().message.isNotBlank())
        }

        tempFile.delete()
    }
}
