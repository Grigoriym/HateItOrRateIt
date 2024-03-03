package com.grappim.hateitorrateit.utils.file

import android.net.Uri

interface FileDeletionUtils {
    suspend fun deleteFolder(folderName: String)
    fun deleteFile(uriString: String): Boolean
    fun deleteFile(uri: Uri): Boolean
    suspend fun clearMainFolder(): Boolean
}
