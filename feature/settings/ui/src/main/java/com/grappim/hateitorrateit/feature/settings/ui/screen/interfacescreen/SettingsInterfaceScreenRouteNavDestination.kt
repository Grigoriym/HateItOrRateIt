package com.grappim.hateitorrateit.feature.settings.ui.screen.interfacescreen

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
data object SettingsInterfaceScreenRouteNavDestination

fun NavController.goToSettingsInterfaceScreen() {
    navigate(route = SettingsInterfaceScreenRouteNavDestination)
}
