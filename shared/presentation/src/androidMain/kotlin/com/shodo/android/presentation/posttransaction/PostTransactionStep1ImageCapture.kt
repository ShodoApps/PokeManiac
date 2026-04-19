package com.shodo.android.presentation.posttransaction

import android.content.Context
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostTransactionStep1ImageCapture(
    platformHandle: Any
) : PostTransactionStep1ImageCapturePort {

    private val context: Context = (platformHandle as Context).applicationContext

    override fun createTempJpegFileAbsolutePath(): Result<String> = runCatching {
        val timeStamp = SimpleDateFormat(DATE_PATTERN, Locale.US).format(Date())
        val file = File.createTempFile("${FILE_PREFIX}_${timeStamp}", FILE_SUFFIX, context.filesDir)
        file.absolutePath
    }

    override fun contentUriStringForAbsolutePath(absolutePath: String): Result<String> = runCatching {
        val file = File(absolutePath)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}$FILE_PROVIDER_AUTHORITY_SUFFIX",
            file
        ).toString()
    }

    private companion object {
        private const val FILE_PREFIX = "PokeManiac_"
        private const val DATE_PATTERN = "yyyyMMdd_HHmmss"
        private const val FILE_SUFFIX = ".jpg"
        private const val FILE_PROVIDER_AUTHORITY_SUFFIX = ".fileprovider"
    }
}
