package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PlatoOutlinedButton(
    imageVector: ImageVector? = null,
    painter: Painter? = null,
    text: String,
    onClicked: () -> Unit
) {
    assert(!(imageVector != null && painter != null)) {
        "Cannot define both imageVector(${imageVector?.name}) and " +
            "painter($painter) simultaneously; please choose only one."
    }
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colors.onPrimary
        ),
        onClick = { onClicked() }
    ) {
        if (painter != null) {
            PlatoIcon(
                modifier = Modifier.size(26.dp),
                painter = painter
            )
        }
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
