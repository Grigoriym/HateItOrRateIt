package com.grappim.hateitorrateit.ui.screens.details

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
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
import com.grappim.ui.widgets.PlatoPagerIndicator
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
        TopImageBarContent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f),
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
private fun TopImageBarContent(
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

    Box(
        modifier = modifier,
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
}

@Composable
private fun DetailsDemonstrationContent(
    modifier: Modifier,
    state: DetailsViewState
) {
    if (state.isEdit.not()) {
        Column(
            modifier = modifier,
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
            Icon(
                imageVector = state.type.icon(),
                contentDescription = "",
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
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = state.nameToEdit,
                onValueChange = state.onSaveName,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                label = {
                    Text(text = stringResource(id = R.string.name_obligatory))
                },
            )

            OutlinedTextField(
                value = state.descriptionToEdit,
                onValueChange = state.onSaveDescription,
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
                onValueChange = state.onSaveShop,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                label = {
                    Text(text = stringResource(id = R.string.shop))
                },
            )

            PlatoHateRateContent(
                currentType = requireNotNull(state.typeToEdit),
                onTypeClicked = state.onTypeChanged,
            )
        }
    }
}
