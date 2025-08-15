@file:OptIn(ExperimentalMaterial3Api::class)

package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.grappim.hateitorrateit.uikit.R
import com.grappim.hateitorrateit.uikit.icons.PlatoIconType
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight

const val PLATO_TOP_BAR_TAG = "plato_top_bar_tag"

@Composable
fun PlatoTopBar(
    goBack: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
    defaultBackButton: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier.testTag(PLATO_TOP_BAR_TAG),
        title = { Text(text = text) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor
        ),
        navigationIcon = {
            val icon = PlatoIconType.ArrowBack.imageVector
            if (defaultBackButton) {
                IconButton(onClick = goBack) {
                    Icon(
                        modifier = Modifier
                            .testTag(icon.name),
                        imageVector = icon,
                        contentDescription = stringResource(
                            id = R.string.content_description_back_button
                        )
                    )
                }
            } else {
                PlatoIconButton(
                    modifier = Modifier
                        .testTag(icon.name),
                    icon = icon,
                    onButtonClick = goBack
                )
            }
        },
        actions = actions
    )
}

@[Composable PreviewDarkLight]
private fun PlatoTopBarPreview() {
    HateItOrRateItTheme {
        PlatoTopBar(
            text = "Some text",
            goBack = {}
        )
    }
}

@[Composable PreviewDarkLight]
private fun PlatoTopBarNotDefaultPreview() {
    HateItOrRateItTheme {
        PlatoTopBar(
            text = "Some text",
            goBack = {},
            defaultBackButton = false
        )
    }
}
