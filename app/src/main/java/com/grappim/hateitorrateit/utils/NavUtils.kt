package com.grappim.hateitorrateit.utils

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry

fun NavBackStackEntry.lifecycleIsResumed(): Boolean =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

fun NavBackStackEntry.safeClick(action: () -> Unit) {
    if (this.lifecycleIsResumed()) {
        action()
    }
}
