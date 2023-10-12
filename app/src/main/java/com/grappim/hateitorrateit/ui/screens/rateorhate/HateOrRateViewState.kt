package com.grappim.hateitorrateit.ui.screens.rateorhate

import com.grappim.hateitorrateit.utils.FileData

data class HateOrRateViewState(
    val filesUris: List<FileData> = emptyList(),
    val documentName: String = "",
    val description: String = "",
    val shop: String = "",

    val setDescription: (description: String) -> Unit,
    val setName: (name: String) -> Unit,
    val setShop: (shop: String) -> Unit,

    val isCreated: Boolean = false,
)