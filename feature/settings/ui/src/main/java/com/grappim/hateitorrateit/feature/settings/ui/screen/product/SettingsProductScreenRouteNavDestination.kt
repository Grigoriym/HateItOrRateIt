package com.grappim.hateitorrateit.feature.settings.ui.screen.product

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
data object SettingsProductScreenRouteNavDestination

fun NavController.goToSettingsProductScreen() {
    navigate(route = SettingsProductScreenRouteNavDestination)
}
