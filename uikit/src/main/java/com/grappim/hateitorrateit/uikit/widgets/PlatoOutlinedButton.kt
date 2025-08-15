package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight

@Composable
fun PlatoOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null
) {
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = { onClick() }
    ) {
        if (imageVector != null) {
            PlatoIcon(
                modifier = Modifier.size(26.dp),
                imageVector = imageVector
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

@Composable
fun PlatoOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    painter: Painter? = null
) {
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = { onClick() }
    ) {
        if (painter != null) {
            PlatoIcon(
                modifier = Modifier.size(26.dp),
                painter = painter
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

@[Composable PreviewDarkLight]
private fun PlatoOutlinedButtonImageVector() {
    HateItOrRateItTheme {
        PlatoOutlinedButton(
            text = "Login",
            onClick = {},
            imageVector = Icons.Default.Clear
        )
    }
}
