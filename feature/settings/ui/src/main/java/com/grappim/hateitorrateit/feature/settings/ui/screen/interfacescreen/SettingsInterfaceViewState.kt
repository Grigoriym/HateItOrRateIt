package com.grappim.hateitorrateit.feature.settings.ui.screen.interfacescreen

import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.utils.ui.NativeText
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

data class SettingsInterfaceViewState(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.default(),
    val onDarkThemeConfigClicked: (DarkThemeConfig) -> Unit = {},
    val localeOptions: ImmutableMap<NativeText, String> = persistentMapOf()
)
