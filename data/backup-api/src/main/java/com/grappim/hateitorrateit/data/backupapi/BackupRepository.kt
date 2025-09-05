package com.grappim.hateitorrateit.data.backupapi

import com.grappim.hateitorrateit.data.backupapi.models.BackupState
import kotlinx.coroutines.flow.Flow

interface BackupRepository {

    /**
     * Creates a backup with progress updates
     * @return Flow of backup state including progress updates and final result
     */
    suspend fun createBackupWithProgress(): Flow<BackupState>
}
