package com.shodo.android.presentation.posttransaction

import com.shodo.android.presentation.PresentationError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Step 1 (camera): wraps [PostTransactionStep1ImageCapturePort]; exposes [PresentationError] for snackbars.
 * Platform UI maps absolute paths ↔ local file types as needed.
 */
class PostTransactionStep1ScreenModel(
    private val imageCapture: PostTransactionStep1ImageCapturePort
) {

    private val _error = MutableSharedFlow<PresentationError>(extraBufferCapacity = 1)
    val error = _error.asSharedFlow()

    fun createTempJpegFileAbsolutePath(): String? {
        return imageCapture.createTempJpegFileAbsolutePath().getOrElse { e ->
            val ex = e as? Exception ?: Exception(e)
            _error.tryEmit(PresentationError.from(ex))
            null
        }
    }

    fun contentUriStringForAbsolutePath(absolutePath: String): String? {
        return imageCapture.contentUriStringForAbsolutePath(absolutePath).getOrElse { e ->
            val ex = e as? Exception ?: Exception(e)
            _error.tryEmit(PresentationError.from(ex))
            null
        }
    }
}
