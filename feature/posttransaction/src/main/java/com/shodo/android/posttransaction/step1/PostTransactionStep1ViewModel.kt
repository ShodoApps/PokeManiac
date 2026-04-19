package com.shodo.android.posttransaction.step1

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.shodo.android.coreui.UiError
import com.shodo.android.presentation.posttransaction.PostTransactionStep1ImageCapturePort
import java.io.File
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PostTransactionStep1ViewModel(
    private val imageCapture: PostTransactionStep1ImageCapturePort
) : ViewModel() {

    // extraBufferCapacity=1 allows tryEmit() from non-suspend callers without orphan launches
    private val _error = MutableSharedFlow<UiError>(extraBufferCapacity = 1)
    val error = _error.asSharedFlow()

    // Note: these functions are called from Compose remember{} which requires a synchronous return.
    // File creation + FileProvider resolution are lightweight (< 1ms) on device.
    fun createImageFile(): File? {
        val path = imageCapture.createTempJpegFileAbsolutePath().getOrElse { e ->
            val ex = e as? Exception ?: Exception(e)
            _error.tryEmit(UiError.from(ex))
            return null
        }
        return File(path)
    }

    fun getUriForFile(file: File?): Uri? {
        if (file == null) return null
        val uriString = imageCapture.contentUriStringForAbsolutePath(file.absolutePath).getOrElse { e ->
            val ex = e as? Exception ?: Exception(e)
            _error.tryEmit(UiError.from(ex))
            return null
        }
        return uriString.toUri()
    }
}
