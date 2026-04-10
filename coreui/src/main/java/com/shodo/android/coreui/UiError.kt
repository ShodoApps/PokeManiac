package com.shodo.android.coreui

data class UiError(val message: String) {
    companion object {
        fun from(e: Exception): UiError = UiError(
            message = e.message?.takeIf { it.isNotBlank() } ?: "Something went wrong"
        )
    }
}
