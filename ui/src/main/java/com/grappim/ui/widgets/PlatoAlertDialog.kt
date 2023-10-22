package com.grappim.ui.widgets

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.grappim.ui.R

@Composable
fun PlatoAlertDialog(
    text: String,
    showAlertDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmButtonClicked: () -> Unit,
    onDismissButtonClicked: () -> Unit,
) {
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = text)
            },
            confirmButton = {
                Button(
                    onClick = onConfirmButtonClicked
                ) {
                    Text(stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismissButtonClicked
                ) {
                    Text(stringResource(id = R.string.no))
                }
            }
        )
    }
}
