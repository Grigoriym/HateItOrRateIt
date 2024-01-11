package com.grappim.hateitorrateit.ui.screens.details

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.ui.R
import com.grappim.hateitorrateit.ui.color
import com.grappim.hateitorrateit.ui.icon
import com.grappim.hateitorrateit.ui.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.ui.utils.PlatoIconType
import com.grappim.hateitorrateit.ui.utils.ThemePreviews
import com.grappim.hateitorrateit.ui.widgets.PlatoAlertDialog
import com.grappim.hateitorrateit.ui.widgets.PlatoCard
import com.grappim.hateitorrateit.ui.widgets.PlatoHateRateContent
import com.grappim.hateitorrateit.ui.widgets.PlatoIcon
import com.grappim.hateitorrateit.ui.widgets.PlatoIconButton
import com.grappim.hateitorrateit.ui.widgets.PlatoPagerIndicator
import com.grappim.hateitorrateit.ui.widgets.PlatoPlaceholderImage
import com.grappim.hateitorrateit.ui.widgets.PlatoProgressIndicator
import com.grappim.hateitorrateit.ui.widgets.PlatoTextButton
import com.grappim.hateitorrateit.ui.widgets.PlatoTopBar
import com.grappim.hateitorrateit.ui.widgets.text.TextH4
import com.grappim.hateitorrateit.utils.models.CameraTakePictureData
import kotlinx.coroutines.launch

const val DETAILS_SCREEN_CONTENT_TAG = "details_screen_content_tag"
const val DETAILS_TOP_APP_BAR_TAG = "details_top_app_bar_tag"
const val DETAILS_EDIT_CONTENT_TAG = "details_edit_content_tag"
const val DETAILS_DEMONSTRATION_CONTENT_TAG = "details_demonstration_content_tag"

private const val TOP_APP_BAR_WEIGHT = 1.2f

@Composable
fun DetailsRoute(
    viewModel: DetailsViewModel = hiltViewModel(),
    goBack: () -> Unit,
    onImageClicked: (productId: String, index: Int) -> Unit,
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    DetailsScreen(
        state = state,
        goBack = goBack,
        onImageClicked = onImageClicked,
    )
}

@Composable
internal fun DetailsScreen(
    state: DetailsViewState,
    goBack: () -> Unit,
    onImageClicked: (productId: String, index: Int) -> Unit,
) {
    LaunchedEffect(state.productDeleted) {
        if (state.productDeleted) {
            goBack()
        }
    }

    if (state.isLoading.not()) {
        DetailsScreenContent(
            state = state,
            goBack = goBack,
            onImageClicked = onImageClicked,
        )
    } else {
        PlatoProgressIndicator(true)
    }
}

@Composable
private fun DetailsScreenContent(
    state: DetailsViewState,
    goBack: () -> Unit,
    onImageClicked: (productId: String, index: Int) -> Unit,
) {
    PlatoAlertDialog(
        text = stringResource(id = R.string.are_you_sure_to_delete_product),
        showAlertDialog = state.showAlertDialog,
        dismissButtonText = stringResource(id = R.string.no),
        onDismissRequest = {
            state.onShowAlertDialog(false)
        },
        onConfirmButtonClicked = {
            state.onDeleteProductConfirm()
        },
        onDismissButtonClicked = {
            state.onShowAlertDialog(false)
        }
    )

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .imePadding()
            .testTag(DETAILS_SCREEN_CONTENT_TAG),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBarContent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(TOP_APP_BAR_WEIGHT),
            state = state,
            onImageClicked = onImageClicked,
            goBack = goBack,
        )

        val detailsInfoModifier = Modifier
            .padding(top = 16.dp)
            .weight(1f)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())

        DetailsDemonstrationContent(
            modifier = detailsInfoModifier,
            state = state,
        )

        DetailsEditContent(
            modifier = detailsInfoModifier,
            state = state
        )
    }
}

@Composable
private fun TopAppBarContent(
    modifier: Modifier = Modifier,
    state: DetailsViewState,
    onImageClicked: (productId: String, index: Int) -> Unit,
    goBack: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var cameraTakePictureData by remember {
        mutableStateOf(CameraTakePictureData.empty())
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        keyboardController?.hide()
        uri?.let {
            state.onAddImageFromGalleryClicked(uri)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess: Boolean ->
        keyboardController?.hide()
        if (isSuccess) {
            state.onAddCameraPictureClicked(cameraTakePictureData)
        }
    }

    val pagerState = rememberPagerState {
        state.images.size
    }

    ScrollToLastImageOnUpdate(state, pagerState)

    Box(
        modifier = modifier
            .testTag(DETAILS_TOP_APP_BAR_TAG),
    ) {
        AppBarImageContent(
            state = state,
            pagerState = pagerState,
            onImageClicked = onImageClicked,
        )

        PlatoPagerIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            show = state.images.size > 1,
            size = state.images.size,
            pagerState = pagerState,
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = 8.dp)
        ) {
            PlatoIconButton(
                icon = PlatoIconType.Camera.imageVector,
                onButtonClick = {
                    keyboardController?.hide()
                    cameraTakePictureData = state.getCameraImageFileUri()
                    cameraLauncher.launch(cameraTakePictureData.uri)
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            PlatoIconButton(
                icon = PlatoIconType.Image.imageVector,
                onButtonClick = {
                    keyboardController?.hide()
                    galleryLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            )
        }

        PlatoTextButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp),
            text = stringResource(id = R.string.delete_image),
            onClick = {
                state.onDeleteImage(pagerState.currentPage)
            }
        )

        AppBarTopButtonsContent(
            state = state,
            goBack = goBack,
        )
    }
}


/**
 * A fix ensuring that the pagerState updates accurately
 * whenever an image is added or deleted.
 * On adding: we move to the last image
 * On deleting we don't do anything
 */
@Composable
private fun ScrollToLastImageOnUpdate(
    state: DetailsViewState,
    pagerState: PagerState
) {
    var firstRecomposition by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(state.images) {
        coroutineScope.launch {
            if (!firstRecomposition && state.images.isNotEmpty() && !state.isDeletingImage) {
                pagerState.animateScrollToPage(state.images.lastIndex)
            }
            firstRecomposition = false
        }
    }
}

@Composable
private fun AppBarTopButtonsContent(
    state: DetailsViewState,
    goBack: () -> Unit,
) {
    PlatoTopBar(
        modifier = Modifier.padding(top = 2.dp),
        goBack = goBack,
        defaultBackButton = false,
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        actions = {
            if (state.isEdit) {
                PlatoIconButton(
                    icon = PlatoIconType.Done.imageVector,
                    onButtonClick = state.onSubmitChanges
                )
                Spacer(modifier = Modifier.width(12.dp))
                PlatoIconButton(
                    icon = PlatoIconType.Close.imageVector,
                    onButtonClick = state.onToggleEditMode
                )
            } else {
                PlatoIconButton(
                    icon = PlatoIconType.Edit.imageVector,
                    onButtonClick = state.onToggleEditMode
                )
                Spacer(modifier = Modifier.width(12.dp))
                PlatoIconButton(
                    icon = PlatoIconType.Delete.imageVector,
                    onButtonClick = state.onDeleteProduct
                )
            }
        })
}

@Composable
private fun BoxScope.AppBarImageContent(
    state: DetailsViewState,
    pagerState: PagerState,
    onImageClicked: (productId: String, index: Int) -> Unit,
) {
    if (state.images.isNotEmpty()) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
            state = pagerState,
        ) { index ->
            val file = state.images[index]
            PlatoCard(
                shape = RoundedCornerShape(
                    bottomEnd = 16.dp,
                    bottomStart = 16.dp,
                ),
                onClick = {
                    if (state.isEdit.not()) {
                        onImageClicked(
                            state.id,
                            index
                        )
                    }
                }
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth(),
                    painter = rememberAsyncImagePainter(file.uriString),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
        }
    } else {
        PlatoPlaceholderImage(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun DetailsDemonstrationContent(
    modifier: Modifier = Modifier,
    state: DetailsViewState
) {
    if (state.isEdit.not()) {
        Column(
            modifier = modifier
                .testTag(DETAILS_DEMONSTRATION_CONTENT_TAG),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            TextH4(text = state.name)

            if (state.description.isNotEmpty()) {
                Text(
                    text = state.description,
                )
            }

            if (state.shop.isNotEmpty()) {
                Text(
                    text = state.shop,
                )
            }

            if (state.createdDate.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    text = state.createdDate
                )
            }

            requireNotNull(state.type)
            PlatoIcon(
                imageVector = state.type.icon(),
                tint = state.type.color(),
            )
        }
    }
}

@Composable
private fun DetailsEditContent(
    modifier: Modifier = Modifier,
    state: DetailsViewState
) {
    if (state.isEdit) {
        Column(
            modifier = modifier
                .testTag(DETAILS_EDIT_CONTENT_TAG),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = state.nameToEdit,
                onValueChange = state.onSetName,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                label = {
                    Text(text = stringResource(id = R.string.name_obligatory))
                },
            )

            OutlinedTextField(
                value = state.descriptionToEdit,
                onValueChange = state.onSetDescription,
                singleLine = false,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                label = {
                    Text(text = stringResource(id = R.string.description))
                },
            )

            OutlinedTextField(
                value = state.shopToEdit,
                onValueChange = state.onSetShop,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                label = {
                    Text(text = stringResource(id = R.string.shop))
                },
            )

            PlatoHateRateContent(
                currentType = requireNotNull(state.typeToEdit),
                onTypeClicked = state.onSetType,
            )
        }
    }
}

@[Composable ThemePreviews]
private fun DetailsScreenPreview() {
    HateItOrRateItTheme {
        DetailsScreen(
            state = getPreviewState(),
            goBack = {},
            onImageClicked = { _, _ -> }
        )
    }
}

@[Composable Preview(showBackground = true)]
private fun DetailsScreenWithLoadingPreview() {
    HateItOrRateItTheme {
        DetailsScreen(
            state = getPreviewState().copy(isLoading = true),
            goBack = {},
            onImageClicked = { _, _ -> }
        )
    }
}

@[Composable ThemePreviews]
private fun TopAppBarContentPreview() {
    HateItOrRateItTheme {
        TopAppBarContent(
            state = getPreviewState(),
            onImageClicked = { _, _ -> },
            goBack = {}
        )
    }
}

@[Composable ThemePreviews]
private fun DetailsEditContentPreview() {
    HateItOrRateItTheme {
        DetailsEditContent(
            state = getPreviewState()
        )
    }
}

@[Composable ThemePreviews]
private fun DetailsDemonstrationContentPreview() {
    HateItOrRateItTheme {
        DetailsDemonstrationContent(
            state = getPreviewState().copy(isEdit = false)
        )
    }
}

private fun getPreviewState() = DetailsViewState(
    id = "accommodare",
    name = "Darren Stanton",
    description = "altera",
    shop = "pulvinar",
    createdDate = "ornare",
    productFolderName = "Estelle Duke",
    images = listOf(),
    type = HateRateType.HATE,
    nameToEdit = "Lorenzo Strickland",
    descriptionToEdit = "disputationi",
    shopToEdit = "iusto",
    typeToEdit = HateRateType.HATE,
    isLoading = false,
    isEdit = true,
    onSetName = {},
    onSetDescription = {},
    onSetShop = {},
    onToggleEditMode = {},
    onSubmitChanges = {},
    onSetType = {},
    showAlertDialog = false,
    onShowAlertDialog = {},
    onDeleteProduct = {},
    productDeleted = false,
    onDeleteProductConfirm = {},
    onDeleteImage = {},
    onAddImageFromGalleryClicked = {},
    onAddCameraPictureClicked = {},
    getCameraImageFileUri = { CameraTakePictureData.empty() })
