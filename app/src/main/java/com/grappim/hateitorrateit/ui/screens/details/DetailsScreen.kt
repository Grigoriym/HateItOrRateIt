package com.grappim.hateitorrateit.ui.screens.details

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.grappim.ui.R
import com.grappim.ui.color
import com.grappim.ui.icon
import com.grappim.ui.widgets.PlatoHateRateContent
import com.grappim.ui.widgets.PlatoIconButton
import com.grappim.ui.widgets.PlatoTopBar
import com.grappim.ui.widgets.text.TextH4

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
        )
    }
}

@Composable
private fun DetailsScreenContent(
    state: DetailsViewState,
    goBack: () -> Unit,
    onDocImageClicked: (uriString: String) -> Unit,
) {
    val pagerState = rememberPagerState {
        state.filesUri.size
    }
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
//                            Timber.d("Clicked on image: ${file.uriString}")
//                            val encodedUrl = URLEncoder.encode(
//                                file.uriString,
//                                StandardCharsets.UTF_8.toString()
//                            )
//                            onDocImageClicked(encodedUrl)
//                        }
                    ,
                    shape = RoundedCornerShape(
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp,
                    )
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

            if (state.filesUri.size > 1) {
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
                                .size(12.dp)
                        )
                    }
                }
            }

            PlatoTopBar(
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
                    }
                })
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
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = state.onSaveName,
                        label = {
                            Text(text = "Name *")
                        })
                } else {
                    TextH4(text = state.name)
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
                        onValueChange = state.onSaveDescription,
                        singleLine = false,
                        label = {
                            Text(text = "Description")
                        }
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
                        onValueChange = state.onSaveDescription,
                        label = {
                            Text(text = stringResource(id = R.string.shop))
                        }
                    )
                } else {
                    Text(
                        text = state.shop,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
            ) {
                state.type?.let {
                    if (state.isEdit) {
                        PlatoHateRateContent(
                            currentType = state.type,
                            onTypeClicked = state.onTypeChanged,
                        )
                    } else {
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
}
