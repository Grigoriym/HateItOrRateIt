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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
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
import com.grappim.hateitorrateit.uikit.widgets.PlatoLoadingDialog
import com.grappim.hateitorrateit.uikit.widgets.PlatoTextButton
import com.grappim.hateitorrateit.uikit.widgets.PlatoTopBar
import com.grappim.hateitorrateit.utils.filesapi.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.ui.NativeText
import com.grappim.hateitorrateit.utils.ui.ObserverAsEvents
import com.grappim.hateitorrateit.utils.ui.asString
import kotlinx.coroutines.launch

@Composable
fun ProductManagerRoute(
    goBack: (isNewProduct: Boolean) -> Unit,
    onProductFinish: (isNewProduct: Boolean) -> Unit,
    viewModel: ProductManagerViewModel = hiltViewModel()
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    ObserverAsEvents(viewModel.snackBarMessage) { snackBarMessage ->
        if (snackBarMessage !is NativeText.Empty) {
            coroutineScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = snackBarMessage.asString(context),
                    actionLabel = context.getString(R.string.close),
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        state.trackOnScreenStart()
        onDispose { }
    }
    ProductManagerScreen(
        state = state,
        goBack = goBack,
        onProductCreate = onProductFinish,
        snackbarHostState = snackbarHostState
    )
}

@Composable
internal fun ProductManagerScreen(
    state: ProductManagerViewState,
    goBack: (isNewProduct: Boolean) -> Unit,
    onProductCreate: (isNewProduct: Boolean) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(state.productSaved) {
        if (state.productSaved) {
            onProductCreate.invoke(state.isNewProduct)
        }
    }

    BackHandler(enabled = true) {
        handleBackAction(state)
    }

    LaunchedEffect(state.forceQuit) {
        if (state.forceQuit) {
            handleBackAction(state)
        }
    }

    LaunchedEffect(state.quitStatus) {
        if (state.quitStatus is QuitStatus.Finish) {
            goBack(state.isNewProduct)
        }
    }

    PlatoLoadingDialog(isLoading = state.quitStatus is QuitStatus.InProgress)

    PlatoAlertDialog(
        text = state.alertDialogText.asString(context = LocalContext.current),
        showAlertDialog = state.showAlertDialog,
        confirmButtonText = stringResource(id = R.string.proceed),
        dismissButtonText = stringResource(id = R.string.dismiss),
        onDismissRequest = {
            state.onShowAlertDialog(false)
        },
        onConfirmButtonClick = {
            state.onForceQuit()
        },
        onDismissButtonClick = {
            state.onShowAlertDialog(false)
        }
    )

    ProductManagerContent(
        state = state,
        snackbarHostState = snackbarHostState
    )
}

/**
 * This function determines the appropriate action when the back button is pressed
 * or when a back navigation event is triggered. It uses the current state of the
 * screen to decide whether to show a confirmation dialog, immediately quit the screen,
 * or perform other actions.
 */
fun handleBackAction(state: ProductManagerViewState) {
    /**
     * Performs actions associated with quitting the screen.
     *
     * This inner function encapsulates the logic for quitting the screen,
     * including hiding the alert dialog and invoking the onQuit callback
     * from the screen's state. This function is called when it's determined
     * that the user can safely exit the screen without additional confirmation.
     */
    fun doOnQuit() {
        state.onShowAlertDialog(false)
        state.onQuit()
    }

    if (state.forceQuit) {
        doOnQuit()
        return
    }

    if (state.productName.isNotEmpty()) {
        // If there are unsaved changes (indicated by non-empty product name),
        // prompt the user with an alert dialog to confirm their intent to quit.
        state.onShowAlertDialog(true)
    } else {
        doOnQuit()
    }
}

@Composable
private fun ProductManagerContent(
    state: ProductManagerViewState,
    snackbarHostState: SnackbarHostState
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

    Scaffold(
        modifier = Modifier
            .imePadding()
            .statusBarsPadding()
            .navigationBarsPadding(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(snackbarData = it)
            }
        },
        topBar = {
            PlatoTopBar(
                text = stringResource(id = R.string.hate_or_rate),
                goBack = {
                    handleBackAction(state)
                }
            )
        },
        bottomBar = {
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
    ) {
        Column(
            modifier = Modifier
                .padding(it)
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
                .padding(top = 16.dp)
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
        ProductManagerContent(state = state, snackbarHostState = remember { SnackbarHostState() })
    }
}

private class StateProvider : PreviewParameterProvider<ProductManagerViewState> {
    override val values: Sequence<ProductManagerViewState>
        get() = sequenceOf(
            ProductManagerViewState(
                images = listOf(),
                productName = "Josefina Guzman",
                description = "persius",
                shop = "libris",
                type = HateRateType.RATE,
                draftProduct = null,
                setDescription = {},
                setName = {},
                setShop = {},
                productSaved = false,
                onDeleteImageClicked = {},
                onAddImageFromGalleryClicked = {},
                onAddCameraPictureClicked = {},
                onQuit = {},
                onProductDone = {},
                getCameraImageFileUri = {
                    CameraTakePictureData.empty()
                },
                onTypeClicked = {},
                forceQuit = false,
                onForceQuit = {},
                showAlertDialog = false,
                onShowAlertDialog = {},
                trackOnScreenStart = {}
            )
        )
}
