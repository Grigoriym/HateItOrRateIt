package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.grappim.hateitorrateit.uikit.R
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.ThemePreviews

const val PLATO_ALERT_DIALOG_TAG = "plato_alert_dialog_tag"

@Composable
fun PlatoAlertDialog(
    modifier: Modifier = Modifier,
    text: String,
    showAlertDialog: Boolean,
    confirmButtonText: String = stringResource(id = R.string.yes),
    dismissButtonText: String? = null,
    onDismissRequest: () -> Unit,
    onConfirmButtonClicked: () -> Unit,
    onDismissButtonClicked: (() -> Unit)? = null
) {
    if (showAlertDialog) {
        val dismissButton: @Composable (() -> Unit)? =
            if (dismissButtonText != null && onDismissButtonClicked != null) {
                {
                    Button(
                        onClick = onDismissButtonClicked
                    ) {
                        Text(dismissButtonText)
                    }
                }
            } else {
                null
            }
        AlertDialog(
            modifier = modifier.testTag(PLATO_ALERT_DIALOG_TAG),
            shape = MaterialTheme.shapes.medium.copy(all = CornerSize(16.dp)),
            onDismissRequest = onDismissRequest,
            title = { Text(text = text) },
            confirmButton = {
                Button(
                    onClick = onConfirmButtonClicked
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = dismissButton
        )
    }
}

@[Composable ThemePreviews]
private fun PlatoAlertDialogPreview() {
    HateItOrRateItTheme {
        PlatoAlertDialog(
            text = "Some text",
            showAlertDialog = true,
            onDismissRequest = {},
            onConfirmButtonClicked = {},
            onDismissButtonClicked = {}
        )
    }
}

@[Composable ThemePreviews]
private fun PlatoAlertDialogWithoutDismissPreview() {
    HateItOrRateItTheme {
        PlatoAlertDialog(
            text = "Some text",
            showAlertDialog = true,
            onDismissRequest = {},
            onConfirmButtonClicked = {}
        )
    }
}
