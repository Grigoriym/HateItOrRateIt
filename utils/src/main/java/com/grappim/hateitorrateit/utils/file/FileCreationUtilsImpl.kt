package com.grappim.hateitorrateit.utils.file

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

private const val BYTE_ARRAY_BUFFER = 4 * 1024

@Singleton
class FileCreationUtilsImpl @Inject constructor(
    private val fileInfoRetriever: FileInfoRetriever,
    @ApplicationContext private val context: Context,
    private val folderPathManager: FolderPathManager
) : FileCreationUtils {

    override fun createFileLocally(uri: Uri, folderName: String): File {
        val extension = fileInfoRetriever.getFileExtension(uri)
        val localFile = File(
            folderPathManager.getMainFolder(folderName),
            fileInfoRetriever.getFileName(extension)
        )
        writeDataToFile(uri, localFile)
        Timber.d("createFileLocally, $localFile")
        return localFile
    }

    @Suppress("NestedBlockDepth")
    private fun writeDataToFile(uri: Uri, newFile: File) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: error("can not create inputStream from $uri")

        inputStream.use { input ->
            val outputStream = FileOutputStream(newFile)
            outputStream.use { output ->
                val buffer = ByteArray(BYTE_ARRAY_BUFFER)
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }
}
