package com.grappim.hateitorrateit.data.backupapi

import android.net.Uri
import com.grappim.hateitorrateit.data.backupapi.models.BackupState
import kotlinx.coroutines.flow.Flow

interface BackupRepository {

    /**
     * Creates a backup at user-selected location with progress updates
     * @param backupFileUri The URI where the backup should be saved
     * @return Flow of backup state including progress updates and final result
     */
    suspend fun createBackupWithProgress(backupFileUri: Uri): Flow<BackupState>
}
