package com.grappim.hateitorrateit.ui.utils

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.grappim.hateitorrateit.ui.theme.darkBackgroundColorForPreview

/**
 * Multipreview annotation that represents light and dark themes. Add this annotation to a
 * composable to render the both themes.
 */
@[Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light theme",
) Preview(
    showBackground = true,
    backgroundColor = darkBackgroundColorForPreview,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark theme",
)]
annotation class ThemePreviews