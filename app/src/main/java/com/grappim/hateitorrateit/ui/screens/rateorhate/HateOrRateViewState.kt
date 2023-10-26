package com.grappim.hateitorrateit.ui.screens.rateorhate

import android.net.Uri
import com.grappim.domain.HateRateType
import com.grappim.hateitorrateit.utils.CameraTakePictureData
import com.grappim.hateitorrateit.utils.DraftDocument
import com.grappim.hateitorrateit.utils.FileData

data class HateOrRateViewState(
    val filesUris: List<FileData> = emptyList(),
    val documentName: String = "",
    val description: String = "",
    val shop: String = "",
    val type: HateRateType = HateRateType.HATE,
    val draftDocument: DraftDocument? = null,

    val setDescription: (description: String) -> Unit,
    val setName: (name: String) -> Unit,
    val setShop: (shop: String) -> Unit,

    val isCreated: Boolean = false,

    val onRemoveFileTriggered: (fileData: FileData) -> Unit,
    val onAddImageFromGalleryClicked: (uri: Uri) -> Unit,
    val onAddCameraPictureClicked: (cameraTakePictureData: CameraTakePictureData) -> Unit,
    val removeData: () -> Unit,
    val saveData: () -> Unit,
    val createDocument: () -> Unit,

    val getCameraImageFileUri: () -> CameraTakePictureData,
    val onTypeClicked: (newType: HateRateType) -> Unit,

    val forceQuit: Boolean = false,
    val onForceQuit: () -> Unit,

    val showAlertDialog: Boolean = false,
    val onShowAlertDialog: (show: Boolean) -> Unit,
)
