package com.grappim.hateitorrateit.data.backupimpl.utils

import android.os.Build
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class BackupEligibilityChecker @Inject constructor(
    private val folderPathManager: FolderPathManager
) {

    fun canCreateBackup(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        true
    } else {
        try {
            val downloadsDir = folderPathManager.getBackupParentFolder()
            val backupChildFolder = folderPathManager.getBackupChildFolderName()
            val hiorDir = File(downloadsDir, backupChildFolder)
            (downloadsDir.exists() || downloadsDir.mkdirs()) &&
                (hiorDir.exists() || hiorDir.mkdirs())
        } catch (e: Exception) {
            Timber.e(e, "Cannot access Downloads/hior directory")
            false
        }
    }
}
