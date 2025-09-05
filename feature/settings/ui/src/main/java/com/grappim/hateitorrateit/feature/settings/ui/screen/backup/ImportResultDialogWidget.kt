package com.grappim.hateitorrateit.feature.settings.ui.screen.backup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.grappim.hateitorrateit.data.backupapi.models.ImportResult
import com.grappim.hateitorrateit.strings.RString

@Composable
internal fun ImportResultDialogWidget(importResult: ImportResult, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            TitleWidget(importResult = importResult)
        },
        text = {
            LazyColumn {
                when (importResult) {
                    is ImportResult.Success -> {
                        item {
                            SuccessWidget(importResult = importResult)
                        }
                    }

                    is ImportResult.PartialSuccess -> {
                        item {
                            Column {
                                Text(
                                    text = stringResource(RString.successfully_imported),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "• ${
                                        stringResource(
                                            RString.products_count,
                                            importResult.importedProducts
                                        )
                                    }"
                                )
                                Text(
                                    "• ${
                                        stringResource(
                                            RString.images_count,
                                            importResult.importedImages
                                        )
                                    }"
                                )
                                if (importResult.importedSettings) {
                                    Text("• ${stringResource(RString.app_settings)}")
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        if (importResult.skippedProducts.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(RString.skipped_products),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            items(importResult.skippedProducts) { productName ->
                                Text(
                                    text = "• $productName",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            item { Spacer(modifier = Modifier.height(8.dp)) }
                        }

                        if (importResult.failedImages.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(RString.failed_images),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            items(importResult.failedImages) { imageName ->
                                Text(
                                    text = "• $imageName",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(bottom = 4.dp),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            item { Spacer(modifier = Modifier.height(8.dp)) }
                        }

                        if (importResult.warnings.isNotEmpty()) {
                            item {
                                Text(
                                    text = stringResource(RString.warnings),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            items(importResult.warnings) { warning ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = warning,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }

                    is ImportResult.Failure -> {
                        item {
                            FailureWidget(importResult = importResult)
                        }
                    }
                }
            }
        },
        confirmButton = {
            ConfirmButtonWidget(onDismiss = onDismiss)
        }
    )
}

@Composable
private fun SuccessWidget(importResult: ImportResult.Success) {
    Column {
        Text(
            text = stringResource(RString.successfully_imported),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "• ${
                stringResource(
                    RString.products_count,
                    importResult.importedProducts
                )
            }"
        )
        Text(
            "• ${
                stringResource(
                    RString.images_count,
                    importResult.importedImages
                )
            }"
        )
        if (importResult.importedSettings) {
            Text("• ${stringResource(RString.app_settings)}")
        }
    }
}

@Composable
private fun FailureWidget(importResult: ImportResult.Failure) {
    Column {
        Text(
            text = stringResource(RString.import_failed_message),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = importResult.message,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(RString.error_type, importResult.error),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ConfirmButtonWidget(onDismiss: () -> Unit) {
    TextButton(onClick = onDismiss) {
        Text(stringResource(RString.ok))
    }
}

@Composable
private fun TitleWidget(importResult: ImportResult) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = when (importResult) {
                is ImportResult.Success -> Icons.Filled.CheckCircle
                is ImportResult.PartialSuccess -> Icons.Filled.Warning
                is ImportResult.Failure -> Icons.Filled.Error
            },
            contentDescription = null,
            tint = when (importResult) {
                is ImportResult.Success -> MaterialTheme.colorScheme.primary
                is ImportResult.PartialSuccess -> MaterialTheme.colorScheme.secondary
                is ImportResult.Failure -> MaterialTheme.colorScheme.error
            }
        )
        Text(
            text = when (importResult) {
                is ImportResult.Success -> stringResource(
                    RString.import_completed_successfully
                )

                is ImportResult.PartialSuccess -> stringResource(
                    RString.import_completed_with_warnings
                )

                is ImportResult.Failure -> stringResource(RString.import_failed)
            }
        )
    }
}
