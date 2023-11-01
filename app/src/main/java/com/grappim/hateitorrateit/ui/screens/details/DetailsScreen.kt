package com.grappim.hateitorrateit.ui.screens.details

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.grappim.hateitorrateit.utils.CameraTakePictureData
import com.grappim.ui.R
import com.grappim.ui.color
import com.grappim.ui.icon
import com.grappim.ui.widgets.PlatoAlertDialog
import com.grappim.ui.widgets.PlatoCard
import com.grappim.ui.widgets.PlatoHateRateContent
import com.grappim.ui.widgets.PlatoIconButton
import com.grappim.ui.widgets.PlatoPlaceholderImage
import com.grappim.ui.widgets.PlatoProgressIndicator
import com.grappim.ui.widgets.PlatoTopBar
import com.grappim.ui.widgets.text.TextH4
import kotlinx.coroutines.launch

@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel = hiltViewModel(),
    goBack: () -> Unit,
    onImageClicked: (productId: String, index: Int) -> Unit,
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

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
    val keyboardController = LocalSoftwareKeyboardController.current

    val pagerState = rememberPagerState {
        state.images.size
    }

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
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f),
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

            if (state.images.size > 1) {
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(state.images.size) { iteration ->
                        val color =
                            if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(
                                    horizontal = 4.dp,
                                    vertical = 6.dp
                                )
                                .clip(CircleShape)
                                .background(color)
                                .size(12.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = 8.dp)
            ) {
                PlatoIconButton(
                    icon = Icons.Filled.Camera,
                    onButtonClick = {
                        keyboardController?.hide()
                        cameraTakePictureData = state.getCameraImageFileUri()
                        cameraLauncher.launch(cameraTakePictureData.uri)
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                PlatoIconButton(
                    icon = Icons.Filled.Image,
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

            Button(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp),
                onClick = {
                    state.onDeleteImage(pagerState.currentPage)
                }
            ) {
                Text(text = stringResource(id = R.string.delete_image))
            }

            PlatoTopBar(
                modifier = Modifier.padding(top = 2.dp),
                goBack = goBack,
                defaultBackButton = false,
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                actions = {
                    if (state.isEdit) {
                        PlatoIconButton(
                            icon = Icons.Filled.Done,
                            onButtonClick = state.onEditSubmit
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        PlatoIconButton(
                            icon = Icons.Filled.Close,
                            onButtonClick = state.toggleEditMode
                        )
                    } else {
                        PlatoIconButton(
                            icon = Icons.Filled.Edit,
                            onButtonClick = state.toggleEditMode
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        PlatoIconButton(
                            icon = Icons.Filled.Delete,
                            onButtonClick = state.onDeleteProduct
                        )
                    }
                })
        }

        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
            ) {
                if (state.isEdit) {
                    OutlinedTextField(
                        value = state.nameToEdit,
                        onValueChange = state.onSaveName,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        label = {
                            Text(text = stringResource(id = R.string.name_obligatory))
                        })
                } else {
                    TextH4(text = state.name)
                }
            }

            Box(
                modifier = Modifier
                    .padding(top = 8.dp),
            ) {
                if (state.isEdit) {
                    OutlinedTextField(
                        value = state.descriptionToEdit,
                        onValueChange = state.onSaveDescription,
                        singleLine = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        label = {
                            Text(text = stringResource(id = R.string.description))
                        }
                    )
                } else {
                    if (state.description.isNotEmpty()) {
                        Text(
                            text = state.description,
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(top = 8.dp),
            ) {
                if (state.isEdit) {
                    OutlinedTextField(
                        value = state.shopToEdit,
                        onValueChange = state.onSaveShop,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        label = {
                            Text(text = stringResource(id = R.string.shop))
                        }
                    )
                } else {
                    if (state.shop.isNotEmpty()) {
                        Text(
                            text = state.shop,
                        )
                    }
                }
            }

            if (state.isEdit.not() && state.createdDate.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    text = state.createdDate
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
            ) {
                if (state.isEdit) {
                    PlatoHateRateContent(
                        currentType = requireNotNull(state.typeToEdit),
                        onTypeClicked = state.onTypeChanged,
                    )
                } else {
                    requireNotNull(state.type)
                    Icon(
                        imageVector = state.type.icon(),
                        contentDescription = "",
                        tint = state.type.color(),
                    )
                }
            }
        }
    }
}
