package com.grappim.hateitorrateit.utils.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Extension function to safely find the Activity from a Context.
 * It traverses the ContextWrapper hierarchy to handle cases like BridgeContext in Previews.
 */
fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

fun Context.findActivityOrNull(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
