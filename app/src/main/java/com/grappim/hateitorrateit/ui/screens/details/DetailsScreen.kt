package com.grappim.hateitorrateit.ui.screens.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.grappim.ui.widgets.TopAppBarIconButton
import com.grappim.ui.widgets.text.TextHeadlineLarge
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel = hiltViewModel(),
    goBack: () -> Unit,
    onDocImageClicked: (uriString: String) -> Unit,
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    if (state.isLoading) {
        CircularProgressIndicator()
    } else {
        DetailsScreenContent(
            state = state,
            goBack = goBack,
            onDocImageClicked = onDocImageClicked,
            onEditModeClicked = viewModel::toggleEditMode,
            onEditSubmit = viewModel::onEditSubmit,
            onEditCancel = viewModel::onEditCancel
        )
    }
}

@Composable
private fun DetailsScreenContent(
    state: DetailsViewState,
    goBack: () -> Unit,
    onDocImageClicked: (uriString: String) -> Unit,
    onEditModeClicked: () -> Unit,
    onEditSubmit: () -> Unit,
    onEditCancel: () -> Unit,
) {
    val pagerState = rememberPagerState {
        state.filesUri.size
    }
    Column(
        modifier = Modifier
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f),
        ) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter),
                state = pagerState,
            ) { page ->
                val file = state.filesUri[page]
                Card(
                    modifier = Modifier
//                        .clickable {
//                            val encodedUrl = URLEncoder.encode(
//                                file.uriString,
//                                StandardCharsets.UTF_8.toString()
//                            )
//                            onDocImageClicked(encodedUrl)
//                        }
                    ,
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

            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(state.filesUri.size) { iteration ->
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
                            .size(16.dp)
                    )
                }
            }

            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {},
                navigationIcon = {
                    TopAppBarIconButton(icon = Icons.Filled.ArrowBack, onButtonClick = goBack)
                },
                actions = {
                    if (state.isEdit) {
                        TopAppBarIconButton(icon = Icons.Filled.Done, onButtonClick = onEditSubmit)
                        TopAppBarIconButton(icon = Icons.Filled.Close, onButtonClick = onEditCancel)
                    } else {
                        TopAppBarIconButton(
                            icon = Icons.Filled.Edit,
                            onButtonClick = onEditModeClicked
                        )
                    }
                },
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
            ) {
                if (state.isEdit) {
                    OutlinedTextField(value = state.name,
                        onValueChange = { newValue ->
                            state.onSaveName(newValue)
                        })
                } else {
                    TextHeadlineLarge(text = state.name)
                }
            }

            Text(
                modifier = Modifier
                    .padding(top = 8.dp),
                text = state.createdDate
            )

            Box(
                modifier = Modifier
                    .padding(top = 8.dp),
            ) {
                if (state.isEdit) {
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { newValue ->
                            state.onSaveDescription(newValue)
                        },
                    )
                } else {
                    Text(
                        text = state.description,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(top = 8.dp),
            ) {
                if (state.isEdit) {
                    OutlinedTextField(
                        value = state.shop,
                        onValueChange = { newValue ->
                            state.onSaveDescription(newValue)
                        },
                    )
                } else {
                    Text(
                        text = state.shop,
                    )
                }
            }
        }
    }
}
