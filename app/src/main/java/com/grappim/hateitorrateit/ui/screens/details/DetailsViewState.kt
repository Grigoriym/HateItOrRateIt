package com.grappim.hateitorrateit.ui.screens.details

import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.ProductImageData

data class DetailsViewState(
    val productId: String = "",
    val name: String = "",
    val description: String = "",
    val shop: String = "",
    val createdDate: String = "",
    val productFolderName: String = "",
    val images: List<ProductImageData> = emptyList(),
    val type: HateRateType? = null,

    val isLoading: Boolean = true,

    val showAlertDialog: Boolean = false,
    val onShowAlertDialog: (show: Boolean) -> Unit,
    val onDeleteProduct: () -> Unit,

    val productDeleted: Boolean = false,
    val onDeleteProductConfirm: () -> Unit,

    val updateProduct: () -> Unit,

    val trackScreenStart: () -> Unit,
    val trackEditButtonClicked: () -> Unit,
)
