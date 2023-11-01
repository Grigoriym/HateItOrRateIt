package com.grappim.hateitorrateit.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.grappim.domain.HateRateType
import com.grappim.hateitorrateit.model.DocumentListUI
import com.grappim.ui.R
import com.grappim.ui.color
import com.grappim.ui.icon
import com.grappim.ui.widgets.PlatoCard
import com.grappim.ui.widgets.PlatoPlaceholderImage
import com.grappim.ui.widgets.text.TextH5

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onDocumentClick: (id: Long) -> Unit
) {
    val state by viewModel.viewState.collectAsState()
    HomeScreenContent(
        state = state,
        onDocumentClick = onDocumentClick,
    )
}

@Composable
private fun HomeScreenContent(
    state: HomeViewState,
    onDocumentClick: (id: Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        SearchContent(
            state = state,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = state.selectedType == HateRateType.HATE,
                onClick = {
                    state.onFilterSelected(HateRateType.HATE)
                },
                leadingIcon = if (state.selectedType == HateRateType.HATE) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                        )
                    }
                } else {
                    null
                }) {
                Text("Hate")
            }
            FilterChip(
                selected = state.selectedType == HateRateType.RATE,
                onClick = {
                    state.onFilterSelected(HateRateType.RATE)
                },
                leadingIcon = if (state.selectedType == HateRateType.RATE) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                        )
                    }
                } else {
                    null
                }) {
                Text("Rate")
            }
        }
        LazyColumn(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxSize()
        ) {
            items(state.docs) { document ->
                DocItem(document, onDocumentClick)
            }
        }
    }
}

@Composable
private fun SearchContent(
    modifier: Modifier = Modifier,
    state: HomeViewState,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        modifier = modifier
            .fillMaxWidth(),
        value = state.query,
        onValueChange = state.onSearchQueryChanged,
        shape = RoundedCornerShape(10.dp),
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "search button")
        },
        trailingIcon = {
            if (state.query.isNotEmpty()) {
                IconButton(onClick = {
                    keyboardController?.hide()
                    state.onClearQueryClicked()
                }) {
                    Icon(imageVector = Icons.Filled.Cancel, contentDescription = "cancel button")
                }
            }
        },
        placeholder = {
            Text(text = stringResource(id = R.string.search))
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
            }
        )
    )
}

@Composable
private fun DocItem(
    document: DocumentListUI,
    onDocumentClick: (id: Long) -> Unit,
) {
    PlatoCard(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(200.dp),
        onClick = {
            onDocumentClick(document.id.toLong())
        },
    ) {
        Box {
            if (document.previewUriString.isEmpty()) {
                PlatoPlaceholderImage(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxSize()
                )
            } else {
                Image(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxSize(),
                    painter = rememberAsyncImagePainter(model = document.previewUriString),
                    contentScale = ContentScale.Crop,
                    contentDescription = "",
                )
            }

            PlatoCard(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp)
                    .wrapContentWidth()
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = MaterialTheme.colors.surface.copy(
                    alpha = 0.4f
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        TextH5(
                            text = document.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .wrapContentWidth()
                    ) {
                        Icon(
                            imageVector = document.type.icon(),
                            contentDescription = "",
                            tint = document.type.color(),
                        )
                    }
                }
            }
        }
    }
}
