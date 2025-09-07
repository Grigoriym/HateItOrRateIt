package com.grappim.hateitorrateit.feature.settings.ui.screen.database

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
data object SettingsDatabaseScreenRouteNavDestination

fun NavController.goToSettingsDatabaseScreen() {
    navigate(route = SettingsDatabaseScreenRouteNavDestination)
}
