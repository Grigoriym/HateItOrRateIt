package com.grappim.hateitorrateit.data.backupapi

import android.net.Uri
import com.grappim.hateitorrateit.data.backupapi.models.ImportMode
import com.grappim.hateitorrateit.data.backupapi.models.ImportResult
import com.grappim.hateitorrateit.data.backupapi.models.ImportState
import kotlinx.coroutines.flow.Flow

interface ImportRepository {

    /**
     * Imports data from a backup file with progress updates
     * @param backupFileUri URI of the backup file to import
     * @param importMode How to handle existing products during import
     * @return Flow of import state including progress updates and final result
     */
    suspend fun importBackupWithProgress(
        backupFileUri: Uri,
        importMode: ImportMode = ImportMode.CREATE_NEW
    ): Flow<ImportState>
}
