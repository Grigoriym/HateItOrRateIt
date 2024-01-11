package com.grappim.hateitorrateit.ui.screens.rateorhate

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.grappim.hateitorrateit.core.LaunchedEffectResult
import com.grappim.hateitorrateit.core.NativeText
import com.grappim.hateitorrateit.core.asString
import com.grappim.hateitorrateit.ui.R
import com.grappim.hateitorrateit.ui.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.ui.utils.PlatoIconType
import com.grappim.hateitorrateit.ui.utils.ThemePreviews
import com.grappim.hateitorrateit.ui.widgets.PlatoAlertDialog
import com.grappim.hateitorrateit.ui.widgets.PlatoCard
import com.grappim.hateitorrateit.ui.widgets.PlatoHateRateContent
import com.grappim.hateitorrateit.ui.widgets.PlatoIconButton
import com.grappim.hateitorrateit.ui.widgets.PlatoTextButton
import com.grappim.hateitorrateit.ui.widgets.PlatoTopBar
import com.grappim.hateitorrateit.utils.models.CameraTakePictureData
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
internal fun HateOrRateRoute(
    goBack: () -> Unit,
    onProductCreated: () -> Unit,
    viewModel: HateOrRateViewModel = hiltViewModel(),
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val snackBarMessage by viewModel.snackBarMessage.collectAsState(
        initial = LaunchedEffectResult(
            data = NativeText.Empty,
            timestamp = 0L
        )
    )
    HateOrRateScreen(
        state = state,
        goBack = goBack,
        onProductCreated = onProductCreated,
        snackBarMessage = snackBarMessage,
    )
}

@Composable
internal fun HateOrRateScreen(
    state: HateOrRateViewState,
    goBack: () -> Unit,
    onProductCreated: () -> Unit,
    snackBarMessage: LaunchedEffectResult<out NativeText>,
) {
    LaunchedEffect(state.isCreated) {
        if (state.isCreated) {
            onProductCreated.invoke()
        }
    }

    BackHandler(enabled = true) {
        handleBackAction(state, goBack)
    }

    LaunchedEffect(state.forceQuit) {
        if (state.forceQuit) {
            handleBackAction(state, goBack)
        }
    }

    PlatoAlertDialog(
        text = stringResource(id = R.string.if_quit_lose_data),
        showAlertDialog = state.showAlertDialog,
        confirmButtonText = stringResource(id = R.string.ok),
        onDismissRequest = {
            state.onShowAlertDialog(false)
        },
        onConfirmButtonClicked = {
            state.onForceQuit()
        },
        onDismissButtonClicked = {
            state.onShowAlertDialog(false)
        }
    )

    RateOrHateScreenContent(
        state = state,
        goBack = goBack,
        snackBarMessage = snackBarMessage,
    )
}

fun handleBackAction(state: HateOrRateViewState, goBack: () -> Unit) {
    fun doOnQuit() {
        state.onShowAlertDialog(false)
        state.removeData()
        goBack()
    }

    if (state.forceQuit) {
        doOnQuit()
        return
    }

    if (state.images.isNotEmpty() || state.productName.isNotEmpty()) {
        state.onShowAlertDialog(true)
    } else {
        doOnQuit()
    }
}

@Composable
private fun RateOrHateScreenContent(
    state: HateOrRateViewState,
    goBack: () -> Unit,
    snackBarMessage: LaunchedEffectResult<out NativeText>,
) {
    val context = LocalContext.current
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

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackBarMessage) {
        if (snackBarMessage.data !is NativeText.Empty) {
            val result = snackbarHostState.showSnackbar(
                message = snackBarMessage.data.asString(context),
                actionLabel = context.getString(R.string.close),
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) {
                snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .imePadding()
            .statusBarsPadding()
            .navigationBarsPadding(),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            PlatoTopBar(
                text = stringResource(id = R.string.hate_or_rate),
                goBack = {
                    handleBackAction(state, goBack)
                },
            )
        },
        bottomBar = {
            BottomBarButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 12.dp,
                        bottom = 16.dp,
                    )
                    .padding(horizontal = 16.dp),
                state = state
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 4.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            TextFieldsContent(
                modifier = Modifier.padding(horizontal = 16.dp),
                state = state,
            )

            PlatoHateRateContent(
                currentType = state.type,
                onTypeClicked = state.onTypeClicked,
            )

            AddFromContent(
                modifier = Modifier.padding(horizontal = 16.dp),
                onCameraClicked = {
                    keyboardController?.hide()
                    cameraTakePictureData = state.getCameraImageFileUri()
                    cameraLauncher.launch(cameraTakePictureData.uri)
                },
                onGalleryClicked = {
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
                state = state,
            )
        }
    }
}

@Composable
private fun AddFromContent(
    modifier: Modifier = Modifier,
    onCameraClicked: () -> Unit,
    onGalleryClicked: () -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.add_picture_from),
        )
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PlatoTextButton(
                text = stringResource(id = R.string.camera),
                onClick = onCameraClicked,
            )
            PlatoTextButton(
                text = stringResource(id = R.string.gallery),
                onClick = onGalleryClicked,
            )
        }
    }
}

@Composable
private fun ImagesList(
    modifier: Modifier = Modifier,
    state: HateOrRateViewState,
) {
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
        val file = state.images[page]
        PlatoCard(
            modifier = Modifier
                .graphicsLayer {
                    val pageOffset = ((pagerState.currentPage - page)
                            + pagerState.currentPageOffsetFraction).absoluteValue
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .size(width = 350.dp, height = 300.dp)
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = rememberAsyncImagePainter(file.uri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                PlatoIconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(
                            top = 8.dp,
                            end = 8.dp,
                        ),
                    icon = PlatoIconType.Delete.imageVector,
                    onButtonClick = {
                        state.onRemoveImageTriggered(file)
                    },
                )
            }
        }
    }
}

@Composable
private fun BottomBarButton(
    modifier: Modifier = Modifier,
    state: HateOrRateViewState,
) {

    PlatoTextButton(
        modifier = modifier
            .height(42.dp),
        text = stringResource(id = R.string.create),
        onClick = state.createProduct,
    )
}

@Composable
private fun TextFieldsContent(
    modifier: Modifier = Modifier,
    state: HateOrRateViewState,
) {
    Column(
        modifier = modifier
    ) {
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

@[Composable ThemePreviews]
private fun BottomBarButtonPreview() {
    HateItOrRateItTheme {
        BottomBarButton(
            state = HateOrRateViewState.getStateForComposePreview()
        )
    }
}

@[Composable ThemePreviews]
private fun TextFieldsContentPreview() {
    HateItOrRateItTheme {
        TextFieldsContent(
            state = HateOrRateViewState.getStateForComposePreview()
        )
    }
}

@[Composable ThemePreviews]
private fun AddFromContentPreview() {
    HateItOrRateItTheme {
        AddFromContent(
            onCameraClicked = {},
            onGalleryClicked = {}
        )
    }
}

@[Composable ThemePreviews]
private fun RateOrHateScreenContentPreview() {
    HateItOrRateItTheme {
        RateOrHateScreenContent(
            state = HateOrRateViewState.getStateForComposePreview(),
            goBack = {},
            snackBarMessage = LaunchedEffectResult(NativeText.Empty)
        )
    }
}
