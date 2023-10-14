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
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.grappim.hateitorrateit.core.HateRateType
import com.grappim.hateitorrateit.model.DocumentListUI
import com.grappim.hateitorrateit.utils.color
import com.grappim.hateitorrateit.utils.icon
import com.grappim.ui.widgets.text.TextH5

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onDocumentClick: (id: Long) -> Unit
) {
    val documents by viewModel.docs.collectAsState()
    val query by viewModel.query.collectAsState()
    HomeScreenContent(
        query = query,
        setQuery = viewModel::onSearchQueryChanged,
        documents = documents,
        onDocumentClick = onDocumentClick,
        onClearClicked = viewModel::clearQuery,
    )
}

@Composable
private fun HomeScreenContent(
    query: String,
    setQuery: (query: String) -> Unit,
    documents: List<DocumentListUI>,
    onDocumentClick: (id: Long) -> Unit,
    onClearClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        SearchContent(
            query = query,
            setQuery = setQuery,
            onClearClicked = onClearClicked,
        )
        LazyColumn(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxSize()
        ) {
            items(documents) { document ->
                DocItem(document, onDocumentClick)
            }
        }
    }
}

@Composable
private fun SearchContent(
    modifier: Modifier = Modifier,
    query: String,
    setQuery: (query: String) -> Unit,
    onClearClicked: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        modifier = modifier
            .fillMaxWidth(),
        value = query,
        onValueChange = {
            setQuery(it)
        },
        shape = RoundedCornerShape(10.dp),
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearClicked) {
                    Icon(imageVector = Icons.Filled.Cancel, contentDescription = "")
                }
            }
        },
        placeholder = {
            Text(text = "Search")
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
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = {
            onDocumentClick(document.id.toLong())
        },
    ) {
        Box {
            Image(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize(),
                painter = rememberAsyncImagePainter(model = document.preview),
                contentScale = ContentScale.Crop,
                contentDescription = "",
            )

            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp)
                    .wrapContentWidth()
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(20.dp),
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
                        if (document.type == HateRateType.HATE) {
                            Icon(
                                imageVector = document.type.icon(),
                                contentDescription = "",
                                tint = document.type.color(),
                            )
                        } else {
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
}
