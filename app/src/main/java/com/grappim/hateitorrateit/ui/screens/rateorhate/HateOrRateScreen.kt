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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.grappim.hateitorrateit.core.LaunchedEffectResult
import com.grappim.hateitorrateit.core.NativeText
import com.grappim.hateitorrateit.core.asString
import com.grappim.hateitorrateit.utils.CameraTakePictureData
import com.grappim.hateitorrateit.utils.FileData
import com.grappim.ui.widgets.PlatoAlertDialog
import com.grappim.ui.widgets.PlatoIconButton
import com.grappim.ui.widgets.PlatoTopBar
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun RateOrHateScreen(
    viewModel: HateOrRateViewModel = hiltViewModel(),
    goBack: () -> Unit,
    onDocumentCreated: () -> Unit,
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isCreated) {
        if (state.isCreated) {
            onDocumentCreated.invoke()
        }
    }

    var backEnabled by remember {
        mutableStateOf(true)
    }
    var showAlertDialog by remember {
        mutableStateOf(false)
    }

    BackHandler(
        enabled = backEnabled
    ) {
        if (state.filesUris.isNotEmpty()) {
            showAlertDialog = true
        } else {
            backEnabled = false
            state.removeData()
            goBack()
        }
    }

    PlatoAlertDialog(
        showAlertDialog = showAlertDialog,
        onDismissRequest = {
            showAlertDialog = false
        },
        onConfirmButtonClicked = {
            showAlertDialog = false
            backEnabled = false
            state.saveData()
        },
        onDismissButtonClicked = {
            showAlertDialog = false
            backEnabled = false
            state.removeData()
            goBack()
        }
    )

    RateOrHateScreenContent(
        viewModel = viewModel,
        state = state,
        goBack = goBack,
    )
}

@Composable
private fun RateOrHateScreenContent(
    viewModel: HateOrRateViewModel,
    state: HateOrRateViewState,
    goBack: () -> Unit,
) {
    val context = LocalContext.current

    var cameraTakePictureData by remember {
        mutableStateOf(CameraTakePictureData.empty())
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            state.onAddImageFromGalleryClicked(uri)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess: Boolean ->
        if (isSuccess) {
            state.onAddCameraPictureClicked(cameraTakePictureData)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarMessage by viewModel.snackBarMessage.collectAsState(
        initial = LaunchedEffectResult(
            data = NativeText.Empty,
            timestamp = 0L
        )
    )

    LaunchedEffect(snackBarMessage) {
        if (snackBarMessage.data !is NativeText.Empty) {
            snackbarHostState.showSnackbar(snackBarMessage.data.asString(context))
        }
    }

    Scaffold(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding()
            .padding(
                horizontal = 16.dp
            ),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            PlatoTopBar(text = "Hate Or Rate", goBack = goBack)
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 12.dp,
                        bottom = 16.dp
                    )
                    .height(42.dp),
                onClick = {
                    state.createDocument()
                }
            ) {
                Text(text = "Create")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                value = state.documentName,
                onValueChange = { newValue ->
                    state.setName(newValue)
                },
                singleLine = true,
                label = {
                    Text(text = "Name")
                }
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                value = state.description,
                onValueChange = { newValue ->
                    state.setDescription(newValue)
                },
                singleLine = false,
                label = {
                    Text(text = "Description")
                }
            )

            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                value = state.shop,
                onValueChange = { newValue ->
                    state.setShop(newValue)
                },
                singleLine = true,
                label = {
                    Text(text = "Shop")
                }
            )

            AddFromContent(
                onCameraClicked = {
                    cameraTakePictureData = state.getCameraImageFileUri()
                    cameraLauncher.launch(cameraTakePictureData.uri)
                },
                onGalleryClicked = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            )

            FilesList(
                fileUris = state.filesUris,
                onFileRemoved = state.onRemoveFileTriggered,
            )
        }
    }
}

@Composable
private fun AddFromContent(
    onCameraClicked: () -> Unit,
    onGalleryClicked: () -> Unit,
) {
    Text(
        modifier = Modifier
            .padding(top = 16.dp),
        text = "Add picture from:"
    )
    Row(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Button(onClick = onCameraClicked) {
            Text(text = "Camera")
        }
        Button(onClick = onGalleryClicked) {
            Text(text = "Gallery")
        }
    }
}

@Composable
private fun FilesList(
    fileUris: List<FileData>,
    onFileRemoved: (fileData: FileData) -> Unit,
) {
    val pagerState = rememberPagerState {
        fileUris.size
    }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(fileUris) {
        coroutineScope.launch {
            if (fileUris.isNotEmpty()) {
                pagerState.animateScrollToPage(fileUris.lastIndex)
            }
        }
    }

    HorizontalPager(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxSize(),
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        pageSpacing = 8.dp
    ) { page ->
        val file = fileUris[page]
        Card(
            modifier = Modifier
                .graphicsLayer {
                    val pageOffset = ((pagerState.currentPage - page)
                            + pagerState.currentPageOffsetFraction).absoluteValue
                    alpha = lerp(
                        start = 0.4f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
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
                        .align(Alignment.TopEnd),
                    icon = Icons.Filled.Delete,
                    onButtonClick = {
                        onFileRemoved(file)
                    },
                )
            }
        }
    }
}
