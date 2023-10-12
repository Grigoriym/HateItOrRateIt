package com.grappim.hateitorrateit.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.grappim.hateitorrateit.model.DocumentListUI
import com.grappim.ui.widgets.text.TextHTitleLarge
import com.grappim.ui.widgets.text.TextHeadlineLarge

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onDocumentClick: (id: Long) -> Unit
) {
    val documents by viewModel.docs.collectAsState()
    HomeScreenContent(
        documents = documents,
        onDocumentClick = onDocumentClick,
    )
}

@Composable
private fun HomeScreenContent(
    documents: List<DocumentListUI>,
    onDocumentClick: (id: Long) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
    ) {
        items(documents) { document ->
            DocItem(document, onDocumentClick)
        }
    }
}

@Composable
private fun DocItem(
    document: DocumentListUI,
    onDocumentClick: (id: Long) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = {
            onDocumentClick(document.id.toLong())
        },
    ) {
        Box(modifier = Modifier) {
            Image(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(200.dp),
                painter = rememberAsyncImagePainter(model = document.preview),
                contentScale = ContentScale.Crop,
                contentDescription = "",
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black, ),)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextHeadlineLarge(
                    text = document.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                TextHTitleLarge(
                    text = document.shop,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
