package com.grappim.hateitorrateit.ui.screens.details

import android.net.Uri
import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.utils.CameraTakePictureData

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

    val onSaveName: (name: String) -> Unit,
    val onSaveDescription: (description: String) -> Unit,
    val onSaveShop: (shop: String) -> Unit,

    val toggleEditMode: () -> Unit,
    val onEditSubmit: () -> Unit,

    val onTypeChanged: (newType: HateRateType) -> Unit,

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
