package com.grappim.hateitorrateit.feature.settings.ui.screen.about

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
data object SettingsAboutScreenRouteNavDestination

fun NavController.goToSettingsAboutScreen() {
    navigate(route = SettingsAboutScreenRouteNavDestination)
}
