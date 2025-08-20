package com.grappim.hateitorrateit.data.backupapi

import android.net.Uri
import com.grappim.hateitorrateit.data.backupapi.models.ImportResult
import com.grappim.hateitorrateit.data.backupapi.models.ImportState
import kotlinx.coroutines.flow.Flow

interface ImportRepository {

    /**
     * Imports data from a backup file with progress updates
     * @param backupFileUri URI of the backup file to import
     * @return Flow of import state including progress updates and final result
     */
    suspend fun importBackupWithProgress(backupFileUri: Uri): Flow<ImportState>

    /**
     * Validates if a backup file can be imported
     * @param backupFileUri URI of the backup file to validate
     * @return True if backup file is valid and can be imported, false otherwise
     */
    suspend fun canImportBackup(backupFileUri: Uri): Boolean
}
