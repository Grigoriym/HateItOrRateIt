package com.grappim.hateitorrateit.feature.details.ui

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.uikit.color
import com.grappim.hateitorrateit.uikit.icon
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.ThemePreviews
import com.grappim.hateitorrateit.uikit.widgets.PlatoAlertDialog
import com.grappim.hateitorrateit.uikit.widgets.PlatoCard
import com.grappim.hateitorrateit.uikit.widgets.PlatoIcon
import com.grappim.hateitorrateit.uikit.widgets.PlatoIconButton
import com.grappim.hateitorrateit.uikit.widgets.PlatoImage
import com.grappim.hateitorrateit.uikit.widgets.PlatoPagerIndicator
import com.grappim.hateitorrateit.uikit.widgets.PlatoPlaceholderImage
import com.grappim.hateitorrateit.uikit.widgets.PlatoProgressIndicator
import com.grappim.hateitorrateit.uikit.widgets.PlatoTopBar
import com.grappim.hateitorrateit.uikit.widgets.text.TextH4
import com.grappim.hateitorrateit.utils.androidapi.SaveImageState
import com.grappim.hateitorrateit.utils.ui.NativeText
import com.grappim.hateitorrateit.utils.ui.PlatoIconType
import com.grappim.hateitorrateit.utils.ui.asString
import kotlinx.coroutines.launch
import timber.log.Timber

const val DETAILS_SCREEN_CONTENT_TAG = "details_screen_content_tag"
const val DETAILS_TOP_APP_BAR_TAG = "details_top_app_bar_tag"
const val DETAILS_DEMONSTRATION_CONTENT_TAG = "details_demonstration_content_tag"

private const val TOP_APP_BAR_WEIGHT = 1.2f

@Composable
fun DetailsRoute(
    viewModel: DetailsViewModel = hiltViewModel(),
    goBack: () -> Unit,
    onImageClicked: (productId: String, index: Int) -> Unit,
    onEditClicked: (id: Long) -> Unit,
    isFromEdit: Boolean
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    DisposableEffect(Unit) {
        state.trackScreenStart
        onDispose { }
    }
    DetailsScreen(
        state = state,
        goBack = goBack,
        onImageClicked = onImageClicked,
        onEditClicked = onEditClicked,
        isFromEdit = isFromEdit
    )
}

@Composable
internal fun DetailsScreen(
    state: DetailsViewState,
    goBack: () -> Unit,
    onImageClicked: (productId: String, index: Int) -> Unit,
    onEditClicked: (id: Long) -> Unit,
    isFromEdit: Boolean
) {
    LaunchedEffect(state.productDeleted) {
        if (state.productDeleted) {
            goBack()
        }
    }

    LaunchedEffect(isFromEdit) {
        if (isFromEdit) {
            state.updateProduct()
        }
    }

    if (state.isLoading.not()) {
        DetailsScreenContent(
            state = state,
            goBack = goBack,
            onImageClicked = onImageClicked,
            onEditClicked
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
    onEditClicked: (id: Long) -> Unit
) {
    val snackbarHostSate = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.snackbarMessage) {
        if (state.snackbarMessage != null && state.snackbarMessage.data != NativeText.Empty) {
            val result = snackbarHostSate.showSnackbar(
                message = state.snackbarMessage.data.asString(context),
                actionLabel = context.getString(com.grappim.hateitorrateit.uikit.R.string.close)
            )
            if (result == SnackbarResult.ActionPerformed) {
                snackbarHostSate.currentSnackbarData?.dismiss()
            }
            state.setSnackbarMessage(NativeText.Empty)
        }
    }

    LaunchedEffect(state.saveFileToGalleryState) {
        when (state.saveFileToGalleryState) {
            is SaveImageState.Initial -> {}
            is SaveImageState.Success -> {
                state.setSnackbarMessage(NativeText.Resource(R.string.image_saved_in_gallery))
            }

            is SaveImageState.Failure -> {
                state.setSnackbarMessage(NativeText.Resource(R.string.image_saved_in_gallery_error))
            }
        }
        state.resetSaveFileToGalleryState()
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

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag(DETAILS_SCREEN_CONTENT_TAG),
        snackbarHost = { SnackbarHost(hostState = snackbarHostSate) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues),
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
                onEditClicked = onEditClicked
            )

            DetailsDemonstrationContent(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                state = state
            )
        }
    }
}

@Composable
private fun TopAppBarContent(
    modifier: Modifier = Modifier,
    state: DetailsViewState,
    onImageClicked: (productId: String, index: Int) -> Unit,
    goBack: () -> Unit,
    onEditClicked: (id: Long) -> Unit
) {
    val pagerState = rememberPagerState {
        state.images.size
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            state.setCurrentDisplayedImageIndex(page)
        }
    }

    ScrollToLastImageOnUpdate(state, pagerState)

    Box(
        modifier = modifier
            .testTag(DETAILS_TOP_APP_BAR_TAG)
    ) {
        AppBarImageContent(
            state = state,
            pagerState = pagerState,
            onImageClicked = onImageClicked
        )

        PlatoPagerIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            show = state.images.size > 1,
            size = state.images.size,
            pagerState = pagerState
        )

        AppBarTopButtonsContent(
            state = state,
            goBack = goBack,
            onEditClicked = onEditClicked
        )

        ImageInteractionsSection(state)
    }
}

/**
 * On why we use activity for ShareCompat: https://stackoverflow.com/a/11335794/9822532
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun BoxScope.ImageInteractionsSection(state: DetailsViewState) {
    val context = LocalContext.current
    val activity = context as Activity
    val permissionState = rememberPermissionState(
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    LaunchedEffect(state.shareImageIntent) {
        if (state.shareImageIntent != null) {
            try {
                activity.startActivity(state.shareImageIntent)
            } catch (e: ActivityNotFoundException) {
                Timber.e(e)
                state.setSnackbarMessage(NativeText.Resource(R.string.share_image_error))
            } finally {
                state.clearShareImageIntent()
            }
        }
    }

    if (state.currentImage != null) {
        PlatoAlertDialog(
            text = state.permissionsAlertDialogText,
            showAlertDialog = state.showProvidePermissionsAlertDialog,
            confirmButtonText = stringResource(id = R.string.ok),
            onDismissRequest = {
                state.onShowPermissionsAlertDialog(false, null)
            },
            onConfirmButtonClicked = {
                openAppSettings(activity, state)
                state.onShowPermissionsAlertDialog(false, null)
            },
            dismissButtonText = stringResource(id = R.string.cancel),
            onDismissButtonClicked = {
                state.onShowPermissionsAlertDialog(false, null)
            }
        )

        PlatoIconButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 4.dp, end = 4.dp),
            icon = PlatoIconType.Share.imageVector,
            onButtonClick = {
                state.onShareImageClicked(state.currentImage)
            }
        )

        PlatoIconButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 4.dp, start = 4.dp),
            icon = PlatoIconType.Download.imageVector,
            onButtonClick = {
                onDownloadClicked(
                    state = state,
                    permissionState = permissionState,
                    text = context.getString(R.string.provide_permission)
                )
            }
        )
    }
}

private fun openAppSettings(activity: Activity, state: DetailsViewState) {
    val intent = state.appSettingsIntent
    activity.startActivity(intent)
}

@OptIn(ExperimentalPermissionsApi::class)
private fun onDownloadClicked(
    state: DetailsViewState,
    permissionState: PermissionState,
    text: String
) {
    val image = requireNotNull(state.currentImage)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        state.saveFileToGallery(image)
    } else {
        if (permissionState.status.isGranted) {
            state.saveFileToGallery(image)
        } else {
            if (permissionState.status.shouldShowRationale.not()) {
                state.onShowPermissionsAlertDialog(true, text)
            }
            permissionState.launchPermissionRequest()
        }
    }
}

/**
 * A fix ensuring that the pagerState updates accurately
 * whenever an image is added or deleted.
 * On adding: we move to the last image
 * On deleting we don't do anything
 */
@Composable
private fun ScrollToLastImageOnUpdate(state: DetailsViewState, pagerState: PagerState) {
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
}

@Composable
private fun AppBarTopButtonsContent(
    state: DetailsViewState,
    goBack: () -> Unit,
    onEditClicked: (id: Long) -> Unit
) {
    PlatoTopBar(
        modifier = Modifier.padding(top = 2.dp),
        goBack = goBack,
        defaultBackButton = false,
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        actions = {
            PlatoIconButton(
                icon = PlatoIconType.Edit.imageVector,
                onButtonClick = {
                    state.trackEditButtonClicked()
                    onEditClicked(state.productId.toLong())
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            PlatoIconButton(
                icon = PlatoIconType.Delete.imageVector,
                onButtonClick = state.onDeleteProduct
            )
        }
    )
}

@Composable
private fun BoxScope.AppBarImageContent(
    state: DetailsViewState,
    pagerState: PagerState,
    onImageClicked: (productId: String, index: Int) -> Unit
) {
    if (state.images.isNotEmpty()) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
            state = pagerState
        ) { index ->
            val productImage = state.images[index]

            PlatoCard(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(
                    bottomEnd = 16.dp,
                    bottomStart = 16.dp
                ),
                onClick = {
                    onImageClicked(
                        state.productId,
                        index
                    )
                }
            ) {
                PlatoImage(
                    modifier = Modifier.fillMaxWidth(),
                    painter = rememberAsyncImagePainter(productImage.uriString),
                    contentScale = ContentScale.Crop
                )
            }
        }
    } else {
        PlatoPlaceholderImage(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun DetailsDemonstrationContent(modifier: Modifier = Modifier, state: DetailsViewState) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .testTag(DETAILS_DEMONSTRATION_CONTENT_TAG),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        val type = requireNotNull(state.type)
        TextH4(text = state.name)

        if (state.description.isNotEmpty()) {
            Text(text = state.description)
        }

        if (state.shop.isNotEmpty()) {
            Text(text = state.shop)
        }

        if (state.createdDate.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .padding(top = 8.dp),
                text = state.createdDate
            )
        }

        PlatoIcon(
            imageVector = type.icon(),
            tint = type.color()
        )
    }
}

@[Composable ThemePreviews]
private fun DetailsScreenPreview(@PreviewParameter(StateProvider::class) state: DetailsViewState) {
    HateItOrRateItTheme {
        DetailsScreen(
            state = state,
            goBack = {},
            onImageClicked = { _, _ -> },
            onEditClicked = {},
            isFromEdit = false
        )
    }
}

@[Composable Preview(showBackground = true)]
private fun DetailsScreenWithLoadingPreview(
    @PreviewParameter(StateProvider::class) state: DetailsViewState
) {
    HateItOrRateItTheme {
        DetailsScreen(
            state = state.copy(isLoading = true),
            goBack = {},
            onImageClicked = { _, _ -> },
            onEditClicked = {},
            isFromEdit = false
        )
    }
}

@[Composable ThemePreviews]
private fun TopAppBarContentPreview(
    @PreviewParameter(StateProvider::class) state: DetailsViewState
) {
    HateItOrRateItTheme {
        TopAppBarContent(
            state = state,
            onImageClicked = { _, _ -> },
            goBack = {},
            onEditClicked = {}
        )
    }
}

@[Composable ThemePreviews]
private fun DetailsDemonstrationContentPreview(
    @PreviewParameter(StateProvider::class) state: DetailsViewState
) {
    HateItOrRateItTheme {
        DetailsDemonstrationContent(
            state = state
        )
    }
}

private class StateProvider : PreviewParameterProvider<DetailsViewState> {
    override val values: Sequence<DetailsViewState>
        get() = sequenceOf(
            DetailsViewState(
                productId = "accommodare",
                name = "Darren Stanton fn89r qw089h890qwn qw9ej90qw qw90jeqwjn qwe9jqw90e",
                description = "altera",
                shop = "pulvinar",
                createdDate = "ornare",
                productFolderName = "Estelle Duke",
                images = listOf(),
                type = HateRateType.HATE,
                isLoading = false,
                showAlertDialog = false,
                onShowAlertDialog = {},
                onDeleteProduct = {},
                productDeleted = false,
                onDeleteProductConfirm = {},
                updateProduct = {},
                trackEditButtonClicked = {},
                trackScreenStart = {},
                setCurrentDisplayedImageIndex = {},
                setSnackbarMessage = {},
                saveFileToGallery = { _ -> },
                onShareImageClicked = { _ -> },
                clearShareImageIntent = {},
                onShowPermissionsAlertDialog = { _, _ -> },
                appSettingsIntent = Intent(),
                resetSaveFileToGalleryState = {}
            )
        )
}
