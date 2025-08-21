package com.grappim.hateitorrateit.data.backupapi.models

import java.io.File

sealed class BackupResult {
    data class Success(val backupFile: File) : BackupResult()

    data class PartialSuccess(
        val backupFile: File,
        val missingImages: List<String>,
        val warnings: List<String>
    ) : BackupResult()

    data class Failure(val error: BackupError, val message: String) : BackupResult()
}

enum class BackupError {
    DATABASE_ACCESS_ERROR,
    STORAGE_PERMISSION_DENIED,
    INSUFFICIENT_STORAGE_SPACE,
    IMAGE_ACCESS_ERROR,
    ZIP_CREATION_ERROR,
    SERIALIZATION_ERROR,
    UNKNOWN_ERROR
}

sealed class ImportResult {
    data class Success(
        val importedProducts: Int,
        val importedImages: Int,
        val importedSettings: Boolean
    ) : ImportResult()

    data class PartialSuccess(
        val importedProducts: Int,
        val importedImages: Int,
        val importedSettings: Boolean,
        val skippedProducts: List<String>,
        val failedImages: List<String>,
        val warnings: List<String>
    ) : ImportResult()

    data class Failure(val error: ImportError, val message: String) : ImportResult()
}

enum class ImportError {
    FILE_NOT_FOUND,
    INVALID_BACKUP_FILE,
    CORRUPTED_ZIP,
    UNSUPPORTED_VERSION,
    DATABASE_ERROR,
    STORAGE_ERROR,
    DESERIALIZATION_ERROR,
    UNKNOWN_ERROR
}

enum class ImportPhase {
    INITIALIZING,
    VALIDATING_BACKUP,
    EXTRACTING_DATA,
    IMPORTING_PRODUCTS,
    IMPORTING_IMAGES,
    IMPORTING_SETTINGS,
    FINALIZING,
    COMPLETED
}

data class ImportProgress(
    val phase: ImportPhase,
    val itemsProcessed: Int? = null,
    val totalItems: Int? = null,
    val percentComplete: Int = if (totalItems != null && totalItems > 0 && itemsProcessed != null) {
        (itemsProcessed * 100) / totalItems
    } else {
        0
    }
)

sealed class ImportState {
    data class Progress(val progress: ImportProgress) : ImportState()
    data class Completed(val result: ImportResult) : ImportState()
}
