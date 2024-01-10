package com.grappim.hateitorrateit.ui.screens.settings

import com.grappim.hateitorrateit.domain.HateRateType

data class SettingsViewState(
    val isLoading: Boolean = false,
    val type: HateRateType = HateRateType.HATE,
    val showAlertDialog: Boolean = false,
    val isCrashesCollectionEnabled: Boolean = true,

    val setType: () -> Unit,
    val onClearDataClicked: () -> Unit,
    val onAlertDialogConfirmButtonClicked: () -> Unit,
    val onDismissDialog: () -> Unit,
    val onCrashlyticsToggle: () -> Unit
) {
    @Suppress("LongParameterList")
    fun safeCopy(
        isLoading: Boolean = this.isLoading,
        type: HateRateType = this.type,
        showAlertDialog: Boolean = this.showAlertDialog,
        isCrashesCollectionEnabled: Boolean = this.isCrashesCollectionEnabled,
        setType: () -> Unit = this.setType,
        onClearDataClicked: () -> Unit = this.onClearDataClicked,
        onAlertDialogConfirmButtonClicked: () -> Unit = this.onAlertDialogConfirmButtonClicked,
        onDismissDialog: () -> Unit = this.onDismissDialog,
        onCrashlyticsToggle: () -> Unit = this.onCrashlyticsToggle
    ): SettingsViewState {
        require(!(isLoading && showAlertDialog)) { "isLoading and showAlertDialog cannot be true at the same time" }
        return copy(
            isLoading = isLoading,
            type = type,
            showAlertDialog = showAlertDialog,
            isCrashesCollectionEnabled = isCrashesCollectionEnabled,
            setType = setType,
            onClearDataClicked = onClearDataClicked,
            onAlertDialogConfirmButtonClicked = onAlertDialogConfirmButtonClicked,
            onDismissDialog = onDismissDialog,
            onCrashlyticsToggle = onCrashlyticsToggle
        )
    }
}
