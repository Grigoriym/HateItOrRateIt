package com.grappim.hateitorrateit.utils.filesapi.inforetriever

import android.net.Uri
import java.io.File

interface FileInfoRetriever {
    suspend fun findFileInFolder(fileName: String, folderName: String): File
    fun getFileExtension(uri: Uri): String
    fun getMimeType(uri: Uri): String
    fun getFileSize(uri: Uri): Long
    fun getFileName(uri: Uri): String
    fun getFileName(extension: String): String
    fun getBitmapFileName(): String
}
