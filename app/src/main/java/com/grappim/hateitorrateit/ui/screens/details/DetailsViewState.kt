package com.grappim.hateitorrateit.ui.screens.details

import android.net.Uri
import com.grappim.domain.ProductFileData
import com.grappim.domain.HateRateType
import com.grappim.hateitorrateit.utils.CameraTakePictureData

data class DetailsViewState(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val shop: String = "",
    val createdDate: String = "",
    val documentFolderName: String = "",
    val filesUris: List<ProductFileData> = emptyList(),
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
    val onDeleteDocument: () -> Unit,

    val productDeleted: Boolean = false,
    val onDeleteProductConfirm: () -> Unit,

    val onDeleteImage: (pageIndex: Int) -> Unit,

    val onAddImageFromGalleryClicked: (uri: Uri) -> Unit,
    val onAddCameraPictureClicked: (cameraTakePictureData: CameraTakePictureData) -> Unit,
    val getCameraImageFileUri: () -> CameraTakePictureData,
)
