package com.grappim.hateitorrateit.feature.productmanager.ui

import android.net.Uri
import com.grappim.hateitorrateit.data.repoapi.models.DraftProduct
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.utils.filesapi.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.filesapi.models.ProductImageUIData
import com.grappim.hateitorrateit.utils.ui.NativeText
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

/**
 * @param isNewProduct indicates if we creating a new product, the value being true, or editing the
 * product, the value being false
 */
data class ProductManagerViewState(
    val images: PersistentList<ProductImageUIData> = persistentListOf(),
    val productName: String = "",
    val description: String = "",
    val shop: String = "",
    val type: HateRateType = HateRateType.HATE,

    val draftProduct: DraftProduct? = null,

    val editProduct: Product? = null,

    val isNewProduct: Boolean = true,

    val bottomBarButtonText: NativeText = NativeText.Empty,
    val alertDialogText: NativeText = NativeText.Empty,

    val setDescription: (description: String) -> Unit = {},
    val setName: (name: String) -> Unit = {},
    val setShop: (shop: String) -> Unit = {},

    val productSaved: Boolean = false,

    val onDeleteImageClicked: (productImageUIData: ProductImageUIData) -> Unit = {},
    val onAddImageFromGalleryClicked: (uri: Uri) -> Unit = {},
    val onAddCameraPictureClicked: (cameraTakePictureData: CameraTakePictureData) -> Unit = {},
    val onProductDone: () -> Unit = {},

    val getCameraImageFileUri: () -> CameraTakePictureData,
    val onTypeClicked: (newType: HateRateType) -> Unit = {},

    val showAlertDialog: Boolean = false,
    val onShowAlertDialog: (show: Boolean) -> Unit = {},

    val trackOnScreenStart: () -> Unit = {},

    val onGoBack: () -> Unit = {}
)
