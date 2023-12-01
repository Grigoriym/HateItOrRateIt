package com.grappim.hateitorrateit.ui.screens.details

import android.net.Uri
import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.utils.models.CameraTakePictureData

data class DetailsViewState(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val shop: String = "",
    val createdDate: String = "",
    val productFolderName: String = "",
    val images: List<ProductImageData> = emptyList(),
    val type: HateRateType? = null,

    val nameToEdit: String = name,
    val descriptionToEdit: String = description,
    val shopToEdit: String = shop,
    val typeToEdit: HateRateType? = type,

    val isLoading: Boolean = true,
    val isEdit: Boolean = false,

    val onSetName: (name: String) -> Unit,
    val onSetDescription: (description: String) -> Unit,
    val onSetShop: (shop: String) -> Unit,

    val onToggleEditMode: () -> Unit,
    val onSubmitChanges: () -> Unit,

    val onSetType: (newType: HateRateType) -> Unit,

    val showAlertDialog: Boolean = false,
    val onShowAlertDialog: (show: Boolean) -> Unit,
    val onDeleteProduct: () -> Unit,

    val productDeleted: Boolean = false,
    val onDeleteProductConfirm: () -> Unit,

    val onDeleteImage: (pageIndex: Int) -> Unit,

    val onAddImageFromGalleryClicked: (uri: Uri) -> Unit,
    val onAddCameraPictureClicked: (cameraTakePictureData: CameraTakePictureData) -> Unit,
    val getCameraImageFileUri: () -> CameraTakePictureData,
)
