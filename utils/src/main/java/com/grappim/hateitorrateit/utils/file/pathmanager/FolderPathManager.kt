package com.grappim.hateitorrateit.utils.file.pathmanager

import java.io.File

interface FolderPathManager {
    fun getMainFolder(productFolder: String = ""): File

    fun getTempFolderName(folder: String): String

    fun getBackupFolderName(folder: String): String
}
