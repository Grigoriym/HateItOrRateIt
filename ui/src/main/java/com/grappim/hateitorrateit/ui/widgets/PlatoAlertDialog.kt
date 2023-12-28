package com.grappim.hateitorrateit.ui.widgets

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
import com.grappim.hateitorrateit.ui.R

const val PLATO_ALERT_DIALOG_TAG = "plato_alert_dialog_tag"

@Composable
fun PlatoAlertDialog(
    modifier: Modifier = Modifier,
    text: String,
    showAlertDialog: Boolean,
    confirmButtonText: String = stringResource(id = R.string.yes),
    dismissButtonText: String = stringResource(id = R.string.cancel),
    onDismissRequest: () -> Unit,
    onConfirmButtonClicked: () -> Unit,
    onDismissButtonClicked: () -> Unit,
) {
    if (showAlertDialog) {
        AlertDialog(
            modifier = modifier
                .testTag(PLATO_ALERT_DIALOG_TAG),
            shape = MaterialTheme.shapes.medium.copy(
                all = CornerSize(16.dp)
            ),
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = text)
            },
            confirmButton = {
                Button(
                    onClick = onConfirmButtonClicked
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismissButtonClicked
                ) {
                    Text(dismissButtonText)
                }
            }
        )
    }
}
