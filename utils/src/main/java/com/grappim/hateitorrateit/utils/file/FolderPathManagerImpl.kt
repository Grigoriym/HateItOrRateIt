package com.grappim.hateitorrateit.utils.file

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderPathManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FolderPathManager {

    /**
     * /data/data/com.grappim.hateitorrateit/files/products/1_2024-01-23_20-04-41/
     */
    override fun getMainFolder(productFolder: String): File {
        val folder = File(context.filesDir, "products/$productFolder")
        if (folder.exists().not()) {
            folder.mkdirs()
        }
        Timber.d("getMainFolder: $folder")
        return folder
    }

    /**
     * /data/data/com.grappim.hateitorrateit/files/products/1_2024-01-23_20-04-41_temp/
     */
    override fun getTempFolderName(folder: String): String = "${folder}_temp"

    /**
     * /data/data/com.grappim.hateitorrateit/files/products/1_2024-01-23_20-04-41_backup/
     */
    override fun getBackupFolderName(folder: String): String = "${folder}_backup"
}
