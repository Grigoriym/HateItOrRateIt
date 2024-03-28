package com.grappim.hateitorrateit.ui.screens.settings

import com.grappim.hateitorrateit.core.NativeText
import com.grappim.hateitorrateit.domain.DarkThemeConfig
import com.grappim.hateitorrateit.domain.HateRateType

data class SettingsViewState(
    val isLoading: Boolean = false,
    val type: HateRateType = HateRateType.HATE,
    val showAlertDialog: Boolean = false,
    val isCrashesCollectionEnabled: Boolean = true,
    val isAnalyticsCollectionEnabled: Boolean = true,

    val setNewType: () -> Unit,
    val onClearDataClicked: () -> Unit,
    val onAlertDialogConfirmButtonClicked: () -> Unit,
    val onDismissDialog: () -> Unit,
    val onCrashlyticsToggle: () -> Unit,
    val onAnalyticsToggle: () -> Unit,
    val trackScreenStart: () -> Unit,

    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.default(),
    val onDarkThemeConfigClicked: (DarkThemeConfig) -> Unit,

    val githubRepoLink: String = "",
    val privacyPolicyLink: String = "",

    val localeOptions: Map<NativeText, String>
)
