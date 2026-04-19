package com.shodo.android.posttransaction.step1

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.shodo.android.presentation.posttransaction.PostTransactionStep1ScreenModel
import java.io.File
class PostTransactionStep1ViewModel(
    private val screenModel: PostTransactionStep1ScreenModel
) : ViewModel() {

    val error get() = screenModel.error

    fun createImageFile(): File? {
        val path = screenModel.createTempJpegFileAbsolutePath() ?: return null
        return File(path)
    }

    fun getUriForFile(file: File?): Uri? {
        if (file == null) return null
        val uriString = screenModel.contentUriStringForAbsolutePath(file.absolutePath) ?: return null
        return uriString.toUri()
    }
}
