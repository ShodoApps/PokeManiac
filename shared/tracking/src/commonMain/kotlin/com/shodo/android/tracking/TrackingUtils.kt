package com.shodo.android.tracking

class TrackingUtils {

    fun buildEventScreen(screenName: String): Map<String, String> {
        return mapOf(SCREEN_NAME to screenName)
    }

    fun buildEventClick(clickName: String): Map<String, String> {
        return mapOf(CLICK_NAME to clickName)
    }

    private companion object {
        private const val SCREEN_NAME = "screen_name"
        private const val CLICK_NAME = "click_name"
    }
}
