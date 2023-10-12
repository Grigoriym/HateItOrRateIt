package com.grappim.hateitorrateit.ui.screens.details.docimage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter

@Composable
fun DocImageScreen(
    viewModel: DocImageViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    DocImageContent(
        state = state,
        goBack = goBack,
    )
}

@Composable
private fun DocImageContent(
    state: DocImageViewModelState,
    goBack: () -> Unit
) {
    Scaffold {
        Image(
            modifier = Modifier.padding(it)
                .fillMaxSize(),
            painter = rememberAsyncImagePainter(model = state.uri),
            contentDescription = "",
        )
    }
}