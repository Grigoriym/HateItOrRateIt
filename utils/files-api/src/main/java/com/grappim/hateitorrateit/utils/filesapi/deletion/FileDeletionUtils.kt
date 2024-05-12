package com.grappim.hateitorrateit.utils.filesapi.deletion

import android.net.Uri

interface FileDeletionUtils {
    suspend fun deleteFolder(folderName: String)
    suspend fun deleteFile(uriString: String): Boolean
    suspend fun deleteFile(uri: Uri): Boolean
    suspend fun clearMainFolder(): Boolean
}
