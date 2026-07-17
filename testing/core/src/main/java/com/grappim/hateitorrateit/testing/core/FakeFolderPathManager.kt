package com.grappim.hateitorrateit.testing.core

import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import java.io.File

class FakeFolderPathManager(
    mainFolders: List<File> = emptyList()
) : FolderPathManager {

    private val mainFoldersQueue = ArrayDeque(mainFolders)
    val getMainFolderCalls = mutableListOf<String>()

    var tempFolderNameOverride: String? = null
    var backupFolderNameOverride: String? = null
    var backupParentFolderOverride: File? = null
    var backupChildFolderNameOverride: String? = null

    override fun getMainFolder(productFolder: String): File {
        getMainFolderCalls.add(productFolder)
        return if (mainFoldersQueue.size > 1) {
            mainFoldersQueue.removeFirst()
        } else {
            mainFoldersQueue.first()
        }
    }

    override fun getTempFolderName(folder: String): String =
        tempFolderNameOverride ?: "${folder}_temp"

    override fun getBackupFolderName(folder: String): String =
        backupFolderNameOverride ?: "${folder}_backup"

    override fun getBackupParentFolder(): File =
        backupParentFolderOverride ?: error("backupParentFolderOverride is not configured")

    override fun getBackupChildFolderName(): String =
        backupChildFolderNameOverride ?: error("backupChildFolderNameOverride is not configured")
}
