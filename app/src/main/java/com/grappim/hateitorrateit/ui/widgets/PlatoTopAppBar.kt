@file:OptIn(ExperimentalMaterial3Api::class)

package com.grappim.hateitorrateit.ui.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarBackButtonState
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarState
import com.grappim.hateitorrateit.utils.ui.asString

@Composable
fun PlatoTopAppBar(topBarConfig: TopBarConfig, goBack: () -> Unit) {
    val state = topBarConfig.state
    when (state) {
        is TopBarState.Hidden -> {

        }

        is TopBarState.Visible -> {
            VisibleTopAppBar(state = state, goBack = goBack)
        }
    }
}

@Composable
private fun VisibleTopAppBar(state: TopBarState.Visible, goBack: () -> Unit) {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        title = {
            Text(text = state.title.asString(context))
        },
        navigationIcon = {
            val backState = state.topBarBackButtonState
            when (backState) {
                is TopBarBackButtonState.Hidden -> {

                }

                is TopBarBackButtonState.Visible -> {
                    VisibleBackButton(
                        backState = backState,
                        goBack = goBack
                    )
                }
            }
        }
    )
}

@Composable
private fun VisibleBackButton(backState: TopBarBackButtonState.Visible, goBack: () -> Unit) {
    IconButton(
        onClick = {
            backState.overrideBackHandlerAction?.invoke() ?: goBack()
        }
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }
}
