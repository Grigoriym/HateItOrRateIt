package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.grappim.hateitorrateit.uikit.R
import com.grappim.hateitorrateit.uikit.icons.PlatoIconType
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.ThemePreviews

const val PLATO_TOP_BAR_TAG = "plato_top_bar_tag"

@Composable
fun PlatoTopBar(
    modifier: Modifier = Modifier,
    text: String = "",
    goBack: () -> Unit,
    defaultBackButton: Boolean = true,
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier.testTag(PLATO_TOP_BAR_TAG),
        title = { Text(text = text) },
        elevation = elevation,
        backgroundColor = backgroundColor,
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

@[Composable ThemePreviews]
private fun PlatoTopBarPreview() {
    HateItOrRateItTheme {
        PlatoTopBar(
            text = "Some text",
            goBack = {}
        )
    }
}

@[Composable ThemePreviews]
private fun PlatoTopBarNotDefaultPreview() {
    HateItOrRateItTheme {
        PlatoTopBar(
            text = "Some text",
            goBack = {},
            defaultBackButton = false
        )
    }
}
