package com.grappim.hateitorrateit.feature.settings.ui.screen.analytics

data class SettingsAnalyticsViewState(
    val onCrashlyticsToggle: () -> Unit = {},
    val onAnalyticsToggle: () -> Unit = {},
    val isCrashesCollectionEnabled: Boolean = true,
    val isAnalyticsCollectionEnabled: Boolean = true
)
