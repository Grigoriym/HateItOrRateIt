package com.grappim.hateitorrateit.utils.file.creation

import android.net.Uri
import java.io.File

interface FileCreationUtils {
    suspend fun createFileLocally(uri: Uri, folderName: String): File
}
