package com.grappim.hateitorrateit.feature.settings.ui.screen.analytics

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
data object SettingsAnalyticsScreenRouteNavDestination

fun NavController.goToSettingsAnalyticsScreen() {
    navigate(route = SettingsAnalyticsScreenRouteNavDestination)
}
