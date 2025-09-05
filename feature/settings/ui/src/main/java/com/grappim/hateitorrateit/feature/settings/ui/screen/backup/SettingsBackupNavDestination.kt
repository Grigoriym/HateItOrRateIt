package com.grappim.hateitorrateit.feature.settings.ui.screen.backup

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
data object SettingsBackupNavDestination

fun NavController.navigateToSettingsBackup() {
    navigate(route = SettingsBackupNavDestination)
}
