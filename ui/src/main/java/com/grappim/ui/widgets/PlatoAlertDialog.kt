package com.grappim.ui.widgets

import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PlatoAlertDialog(
    showAlertDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmButtonClicked: () -> Unit,
    onDismissButtonClicked: () -> Unit,
) {
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = "Save the changes before exit?")
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