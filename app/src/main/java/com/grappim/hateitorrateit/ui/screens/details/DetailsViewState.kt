package com.grappim.hateitorrateit.ui.screens.details

import com.grappim.domain.HateRateType

data class DetailsViewState(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val shop: String = "",
    val createdDate: String = "",
    val filesUri: List<com.grappim.domain.DocumentFileData> = emptyList(),
    val type: HateRateType? = null,

    val isLoading: Boolean = true,
    val isEdit: Boolean = false,

    val onSaveName: (name: String) -> Unit,
    val onSaveDescription: (description: String) -> Unit,
    val onSaveShop: (shop: String) -> Unit,

    val toggleEditMode: () -> Unit,
    val onEditSubmit: () -> Unit,

    val onTypeChanged: (newType: HateRateType) -> Unit
)
