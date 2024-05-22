package com.grappim.hateitorrateit.feature.details.ui

import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.data.repoapi.models.ProductImage

data class DetailsViewState(
    val productId: String = "",
    val name: String = "",
    val description: String = "",
    val shop: String = "",
    val createdDate: String = "",
    val productFolderName: String = "",
    val images: List<ProductImage> = emptyList(),
    val type: HateRateType? = null,

    val isLoading: Boolean = true,

    val showAlertDialog: Boolean = false,
    val onShowAlertDialog: (show: Boolean) -> Unit,
    val onDeleteProduct: () -> Unit,

    val productDeleted: Boolean = false,
    val onDeleteProductConfirm: () -> Unit,

    val updateProduct: () -> Unit,

    val trackScreenStart: () -> Unit,
    val trackEditButtonClicked: () -> Unit
)
