@file:OptIn(ExperimentalFoundationApi::class)

package com.grappim.hateitorrateit.feature.productmanager.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.feature.productmanager.ui.widgets.PlatoHateRateContent
import com.grappim.hateitorrateit.uikit.R
import com.grappim.hateitorrateit.uikit.icons.PlatoIconType
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight
import com.grappim.hateitorrateit.uikit.widgets.PlatoAlertDialog
import com.grappim.hateitorrateit.uikit.widgets.PlatoCard
import com.grappim.hateitorrateit.uikit.widgets.PlatoIconButton
import com.grappim.hateitorrateit.uikit.widgets.PlatoTextButton
import com.grappim.hateitorrateit.uikit.widgets.topbar.LocalTopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarBackButtonState
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarState
import com.grappim.hateitorrateit.utils.filesapi.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.ui.NativeText
import com.grappim.hateitorrateit.utils.ui.ObserveAsEvents
import com.grappim.hateitorrateit.utils.ui.asString
import kotlinx.coroutines.launch

@Composable
fun ProductManagerRoute(
    goBack: (isNewProduct: Boolean) -> Unit,
    onProductFinish: (isNewProduct: Boolean) -> Unit,
    showActionSnackbar: (NativeText, actionLabel: String?) -> Unit,
    viewModel: ProductManagerViewModel = hiltViewModel()
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val topBarController = LocalTopBarConfig.current

    LaunchedEffect(Unit) {
        topBarController.update(
            TopBarConfig(
                state = TopBarState.Visible(
                    title = NativeText.Resource(R.string.hate_or_rate),
                    topBarBackButtonState = TopBarBackButtonState.Visible(
                        overrideBackHandlerAction = {
                            state.onShowAlertDialog(true)
                        }
                    )
                )
            )
        )
    }

    ObserveAsEvents(viewModel.onBackAction) {
        goBack(state.isNewProduct)
    }

    ObserveAsEvents(viewModel.snackBarMessage) { snackBarMessage ->
        if (snackBarMessage !is NativeText.Empty) {
            showActionSnackbar(snackBarMessage, context.getString(R.string.close))
        }
    }

    LaunchedEffect(state.productSaved) {
        if (state.productSaved) {
            onProductFinish.invoke(state.isNewProduct)
        }
    }

    BackHandler(enabled = true) {
        state.onShowAlertDialog(true)
    }

    DisposableEffect(Unit) {
        state.trackOnScreenStart()
        onDispose {}
    }

    PlatoAlertDialog(
        text = state.alertDialogText.asString(context = LocalContext.current),
        showAlertDialog = state.showAlertDialog,
        confirmButtonText = stringResource(id = R.string.proceed),
        dismissButtonText = stringResource(id = R.string.dismiss),
        onDismissRequest = {
            state.onShowAlertDialog(false)
        },
        onConfirmButtonClick = {
            state.onGoBack()
        },
        onDismissButtonClick = {
            state.onShowAlertDialog(false)
        }
    )

    ProductManagerContent(state = state)
}

@Composable
private fun ProductManagerContent(state: ProductManagerViewState) {
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

    Surface {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    TextFieldsContent(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        state = state
                    )

                    PlatoHateRateContent(
                        currentType = state.type,
                        onTypeClick = state.onTypeClicked
                    )

                    AddFromContent(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onCameraClick = {
                            keyboardController?.hide()
                            cameraTakePictureData = state.getCameraImageFileUri()
                            cameraLauncher.launch(cameraTakePictureData.uri)
                        },
                        onGalleryClick = {
                            keyboardController?.hide()
                            galleryLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    )

                    ImagesList(
                        modifier = Modifier,
                        state = state
                    )
                }
            }
            BottomBarButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 12.dp,
                        bottom = 16.dp
                    )
                    .padding(horizontal = 16.dp),
                state = state
            )
        }
    }
}

@Composable
private fun AddFromContent(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.add_picture_from)
        )
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PlatoTextButton(
                text = stringResource(id = R.string.camera),
                onClick = onCameraClick
            )
            PlatoTextButton(
                text = stringResource(id = R.string.gallery),
                onClick = onGalleryClick
            )
        }
    }
}

@Composable
private fun ImagesList(state: ProductManagerViewState, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState {
        state.images.size
    }

    var firstRecomposition by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(state.images) {
        coroutineScope.launch {
            if (!firstRecomposition && state.images.isNotEmpty()) {
                pagerState.animateScrollToPage(state.images.lastIndex)
            }
            firstRecomposition = false
        }
    }

    HorizontalPager(
        modifier = modifier
            .padding(top = 16.dp, bottom = 16.dp)
            .fillMaxWidth(),
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 32.dp),
        pageSpacing = 8.dp
    ) { page ->
        val image = state.images[page]
        PlatoCard {
            Box(
                modifier = Modifier
                    .size(width = 350.dp, height = 300.dp)
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = rememberAsyncImagePainter(image.uri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                PlatoIconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(
                            top = 8.dp,
                            end = 8.dp
                        ),
                    icon = PlatoIconType.Delete.imageVector,
                    onButtonClick = {
                        state.onDeleteImageClicked(image)
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomBarButton(state: ProductManagerViewState, modifier: Modifier = Modifier) {
    PlatoTextButton(
        modifier = modifier.height(42.dp),
        text = state.bottomBarButtonText.asString(LocalContext.current),
        onClick = state.onProductDone
    )
}

@Composable
private fun TextFieldsContent(state: ProductManagerViewState, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            value = state.productName,
            onValueChange = state.setName,
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.name_obligatory))
            }
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            value = state.description,
            onValueChange = state.setDescription,
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.description))
            }
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            value = state.shop,
            onValueChange = state.setShop,
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.shop))
            }
        )
    }
}

@[Composable PreviewDarkLight]
private fun BottomBarButtonPreview(
    @PreviewParameter(StateProvider::class) state: ProductManagerViewState
) {
    HateItOrRateItTheme {
        BottomBarButton(
            state = state
        )
    }
}

@[Composable PreviewDarkLight]
private fun TextFieldsContentPreview(
    @PreviewParameter(StateProvider::class) state: ProductManagerViewState
) {
    HateItOrRateItTheme {
        TextFieldsContent(
            state = state
        )
    }
}

@[Composable PreviewDarkLight]
private fun AddFromContentPreview() {
    HateItOrRateItTheme {
        AddFromContent(
            onCameraClick = {},
            onGalleryClick = {}
        )
    }
}

@[Composable PreviewDarkLight]
private fun RateOrHateScreenContentPreview(
    @PreviewParameter(StateProvider::class) state: ProductManagerViewState
) {
    HateItOrRateItTheme {
        ProductManagerContent(state = state)
    }
}

private class StateProvider : PreviewParameterProvider<ProductManagerViewState> {
    override val values: Sequence<ProductManagerViewState>
        get() = sequenceOf(
            ProductManagerViewState(
                productName = "Josefina Guzman",
                description = "persius",
                shop = "libris",
                type = HateRateType.RATE,
                draftProduct = null,
                getCameraImageFileUri = {
                    CameraTakePictureData.empty()
                }
            )
        )
}
