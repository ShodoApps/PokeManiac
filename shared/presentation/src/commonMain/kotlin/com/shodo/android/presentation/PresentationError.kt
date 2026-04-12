package com.shodo.android.presentation

/**
 * User-facing error for flows exposed from **shared presentation** (`:shared:presentation`).
 *
 * Use this instead of Android-only types so the same ViewModel can drive Compose, SwiftUI (later), etc.
 * On Android, [message] is typically shown in a snackbar or dialog; mapping to
 * [com.shodo.android.coreui.UiError] is optional if you need a single Android UI error type.
 */
data class PresentationError(val message: String) {
    companion object {
        fun from(e: Throwable): PresentationError = PresentationError(
            message = e.message?.takeIf { it.isNotBlank() } ?: "Something went wrong",
        )
    }
}
