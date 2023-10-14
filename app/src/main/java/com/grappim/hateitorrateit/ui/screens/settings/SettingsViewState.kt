package com.grappim.hateitorrateit.ui.screens.settings

import com.grappim.hateitorrateit.core.HateRateType

data class SettingsViewState(
    val isLoading: Boolean = false,
    val type: HateRateType = HateRateType.HATE,
    val showAlertDialog: Boolean = false,
    val setType: () -> Unit,
    val onClearDataClicked: () -> Unit,
    val onAlertDialogConfirmButtonClicked: () -> Unit,
    val onDismissDialog: () -> Unit,
)
