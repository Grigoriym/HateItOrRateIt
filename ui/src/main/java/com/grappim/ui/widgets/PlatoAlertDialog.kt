package com.grappim.ui.widgets

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

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
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismissButtonClicked
                ) {
                    Text("No")
                }
            }
        )
    }
}