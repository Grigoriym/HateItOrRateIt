package com.grappim.hateitorrateit.utils.androidapi

import android.content.Intent

interface IntentGenerator {
    fun generateIntentToShareImage(uriString: String, mimeType: String): Intent
    fun generateAppSettingsIntent(): Intent
}
