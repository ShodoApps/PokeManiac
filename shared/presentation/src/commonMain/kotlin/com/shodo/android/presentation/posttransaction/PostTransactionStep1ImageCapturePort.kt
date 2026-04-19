package com.shodo.android.presentation.posttransaction

/**
 * Post-transaction step 1: temp camera file + shareable content URI string (camera capture contract).
 * On Android, paths are absolute under app files dir; URI strings match domain opaque `FileSource.localIdentifier`.
 * Android impl: `PostTransactionStep1ImageCapture` in `androidMain`; add other targets when needed.
 */
interface PostTransactionStep1ImageCapturePort {
    fun createTempJpegFileAbsolutePath(): Result<String>

    fun contentUriStringForAbsolutePath(absolutePath: String): Result<String>
}
