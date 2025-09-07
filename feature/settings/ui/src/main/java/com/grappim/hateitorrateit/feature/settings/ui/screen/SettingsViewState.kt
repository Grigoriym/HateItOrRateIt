package com.grappim.hateitorrateit.feature.settings.ui.screen

data class SettingsViewState(val trackScreenStart: () -> Unit = {}, val isFdroidBuild: Boolean)
