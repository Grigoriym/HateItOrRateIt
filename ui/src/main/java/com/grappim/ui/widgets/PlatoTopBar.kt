package com.grappim.ui.widgets

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun PlatoTopBar(
    text: String = "",
    goBack: () -> Unit,
    defaultBackButton: Boolean = true,
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(text = text)
        },
        elevation = elevation,
        backgroundColor = backgroundColor,
        navigationIcon = {
            if (defaultBackButton) {
                IconButton(onClick = goBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Go Back from Hate It"
                    )
                }
            } else {
                PlatoIconButton(
                    icon = Icons.Filled.ArrowBack,
                    onButtonClick = goBack,
                )
            }
        },
        actions = actions,
    )
}
