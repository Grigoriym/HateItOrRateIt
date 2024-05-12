package com.grappim.hateitorrateit.ui.screens.productmanager

import android.net.Uri
import com.grappim.hateitorrateit.domain.DraftProduct
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.Product
import com.grappim.hateitorrateit.utils.filesapi.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.filesapi.models.ImageData
import com.grappim.hateitorrateit.utils.ui.NativeText

/**
 * @param isNewProduct indicates if we creating a new product, the value being true, or editing the
 * product, the value being false
 */
data class ProductManagerViewState(
    val images: List<ImageData> = emptyList(),
    val productName: String = "",
    val description: String = "",
    val shop: String = "",
    val type: HateRateType = HateRateType.HATE,

    val draftProduct: DraftProduct? = null,

    val editProduct: Product? = null,

    val isNewProduct: Boolean = true,

    val bottomBarButtonText: NativeText = NativeText.Empty,
    val alertDialogText: NativeText = NativeText.Empty,

    val setDescription: (description: String) -> Unit,
    val setName: (name: String) -> Unit,
    val setShop: (shop: String) -> Unit,

    val productSaved: Boolean = false,

    val onDeleteImageClicked: (imageData: ImageData) -> Unit,
    val onAddImageFromGalleryClicked: (uri: Uri) -> Unit,
    val onAddCameraPictureClicked: (cameraTakePictureData: CameraTakePictureData) -> Unit,
    val onQuit: () -> Unit,
    val onProductDone: () -> Unit,

    val getCameraImageFileUri: () -> CameraTakePictureData,
    val onTypeClicked: (newType: HateRateType) -> Unit,

    val forceQuit: Boolean = false,
    val onForceQuit: () -> Unit,
    val quitStatus: QuitStatus = QuitStatus.Initial,

    val showAlertDialog: Boolean = false,
    val onShowAlertDialog: (show: Boolean) -> Unit,

    val trackOnScreenStart: () -> Unit
)
