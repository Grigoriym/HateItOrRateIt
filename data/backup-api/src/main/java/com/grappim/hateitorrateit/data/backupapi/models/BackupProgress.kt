package com.grappim.hateitorrateit.data.backupapi.models

data class BackupProgress(val phase: BackupPhase)

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
