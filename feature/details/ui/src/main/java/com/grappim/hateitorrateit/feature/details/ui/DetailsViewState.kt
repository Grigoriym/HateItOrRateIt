package com.grappim.hateitorrateit.feature.details.ui

import android.content.Intent
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import com.grappim.hateitorrateit.utils.androidapi.SaveImageState
import com.grappim.hateitorrateit.utils.ui.LaunchedEffectResult
import com.grappim.hateitorrateit.utils.ui.NativeText
import java.io.File

data class DetailsViewState(
    val productId: String = "",
    val name: String = "",
    val description: String = "",
    val shop: String = "",
    val createdDate: String = "",
    val productFolderName: String = "",
    val images: List<ProductImage> = emptyList(),
    val type: HateRateType? = null,
    val currentImage: ProductImage? = null,
    val snackbarMessage: LaunchedEffectResult<NativeText>? = null,
    val imageFile: File? = null,

    val appSettingsIntent: Intent,

    val isLoading: Boolean = true,

    val showAlertDialog: Boolean = false,
    val onShowAlertDialog: (show: Boolean) -> Unit,
    val onDeleteProduct: () -> Unit,

    val productDeleted: Boolean = false,
    val onDeleteProductConfirm: () -> Unit,

    val updateProduct: () -> Unit,

    val trackScreenStart: () -> Unit,
    val trackEditButtonClicked: () -> Unit,

    val setCurrentDisplayedImageIndex: (Int) -> Unit,
    val setSnackbarMessage: (NativeText) -> Unit,

    val saveFileToGallery: (ProductImage) -> Unit,
    val saveFileToGalleryState: SaveImageState = SaveImageState.Initial,
    val resetSaveFileToGalleryState: () -> Unit,

    val onShareImageClicked: (productImage: ProductImage) -> Unit,
    val shareImageIntent: Intent? = null,
    val clearShareImageIntent: () -> Unit,
    val showProvidePermissionsAlertDialog: Boolean = false,
    val permissionsAlertDialogText: String = "",
    val onShowPermissionsAlertDialog: (show: Boolean, text: String?) -> Unit
)
