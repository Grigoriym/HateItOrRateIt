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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.model.ProductListUI
import com.grappim.hateitorrateit.ui.R
import com.grappim.hateitorrateit.ui.color
import com.grappim.hateitorrateit.ui.icon
import com.grappim.hateitorrateit.ui.utils.PlatoIconType
import com.grappim.hateitorrateit.ui.widgets.PlatoCard
import com.grappim.hateitorrateit.ui.widgets.PlatoIcon
import com.grappim.hateitorrateit.ui.widgets.PlatoPlaceholderImage
import com.grappim.hateitorrateit.ui.widgets.text.TextH5

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), onProductClick: (id: Long) -> Unit) {
    val state by viewModel.viewState.collectAsState()
    DisposableEffect(Unit) {
        state.trackScreenStart()
        onDispose { }
    }

    HomeScreenContent(
        state = state,
        onProductClick = onProductClick
    )
}

@Composable
private fun HomeScreenContent(state: HomeViewState, onProductClick: (id: Long) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        SearchContent(
            state = state
        )
        FilterChipsContent(
            state = state
        )
        LazyColumn(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxSize()
        ) {
            items(state.products) { products ->
                ProductItem(state, products, onProductClick)
            }
        }
    }
}

@Composable
private fun SearchContent(modifier: Modifier = Modifier, state: HomeViewState) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        modifier = modifier
            .fillMaxWidth(),
        value = state.query,
        onValueChange = state.onSearchQueryChanged,
        shape = RoundedCornerShape(10.dp),
        leadingIcon = {
            PlatoIcon(
                imageVector = PlatoIconType.Search.imageVector,
                contentDescription = "search button"
            )
        },
        trailingIcon = {
            if (state.query.isNotEmpty()) {
                IconButton(onClick = {
                    keyboardController?.hide()
                    state.onClearQueryClicked()
                }) {
                    PlatoIcon(
                        imageVector = PlatoIconType.Cancel.imageVector,
                        contentDescription = "cancel button"
                    )
                }
            }
        },
        placeholder = {
            Text(text = stringResource(id = R.string.search))
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
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
private fun ProductItem(
    state: HomeViewState,
    product: ProductListUI,
    onProductClick: (id: Long) -> Unit
) {
    PlatoCard(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(200.dp),
        onClick = {
            state.trackOnProductClicked()
            onProductClick(product.id.toLong())
        }
    ) {
        Box {
            if (product.previewUriString.isEmpty()) {
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
                    painter = rememberAsyncImagePainter(model = product.previewUriString),
                    contentScale = ContentScale.Crop,
                    contentDescription = ""
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
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextH5(
                            text = product.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .wrapContentWidth()
                    ) {
                        PlatoIcon(
                            imageVector = product.type.icon(),
                            tint = product.type.color()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipsContent(modifier: Modifier = Modifier, state: HomeViewState) {
    Row(
        modifier = modifier,
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
                        imageVector = PlatoIconType.Done.imageVector,
                        contentDescription = "Done icon"
                    )
                }
            } else {
                null
            }
        ) {
            Text(stringResource(id = R.string.hate))
        }
        FilterChip(
            selected = state.selectedType == HateRateType.RATE,
            onClick = {
                state.onFilterSelected(HateRateType.RATE)
            },
            leadingIcon = if (state.selectedType == HateRateType.RATE) {
                {
                    Icon(
                        imageVector = PlatoIconType.Done.imageVector,
                        contentDescription = "Done icon"
                    )
                }
            } else {
                null
            }
        ) {
            Text(stringResource(id = R.string.rate))
        }
    }
}
