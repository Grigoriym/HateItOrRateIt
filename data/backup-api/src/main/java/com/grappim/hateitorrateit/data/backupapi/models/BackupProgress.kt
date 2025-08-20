package com.grappim.hateitorrateit.data.backupapi.models

import java.io.File

data class BackupProgress(
    val phase: BackupPhase,
    val itemsProcessed: Int,
    val totalItems: Int,
    val currentItem: String = "",
    val percentComplete: Int = if (totalItems > 0) (itemsProcessed * 100) / totalItems else 0
)

enum class BackupPhase {
    INITIALIZING,
    COLLECTING_DATABASE_DATA,
    COLLECTING_IMAGES,
    CREATING_BACKUP_FILE,
    FINALIZING,
    COMPLETED
}

sealed class BackupState {
    data class Progress(val progress: BackupProgress) : BackupState()
    data class Completed(val result: BackupResult) : BackupState()
}
