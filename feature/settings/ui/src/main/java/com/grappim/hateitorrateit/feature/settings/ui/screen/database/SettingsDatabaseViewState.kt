package com.grappim.hateitorrateit.feature.settings.ui.screen.database

data class SettingsDatabaseViewState(
    val isLoading: Boolean = false,
    val showAlertDialog: Boolean = false,
    val onClearDataClicked: () -> Unit = {},
    val onAlertDialogConfirmButtonClicked: () -> Unit = {},
    val onDismissDialog: () -> Unit = {}
)
