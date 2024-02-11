package com.grappim.hateitorrateit.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.utils.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.models.ImageData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

private const val BYTE_ARRAY_BUFFER = 4 * 1024

@Singleton
class FileUtils @Inject constructor(
    @ApplicationContext private val context: Context,
    private val hashUtils: HashUtils,
    private val dateTimeUtils: DateTimeUtils,
    private val imageDataMapper: ImageDataMapper,
    private val uriParser: UriParser,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * Used when we save an edited Product to remove _temp from uri because
     * the images will be moved to the original folder
     */
    suspend fun prepareEditedImagesToPersist(images: List<ImageData>): List<ProductImageData> =
        images.map { image ->
            if (image.isEdit) {
                val result = imageDataMapper.toProductImageData(image)
                val newUriString = result.uriString.replace("_temp", "")
                val newUriPath = result.uriPath.replace("_temp", "")
                result.copy(
                    uriString = newUriString,
                    uriPath = newUriPath
                )
            } else {
                imageDataMapper.toProductImageData(image)
            }
        }

    fun getFileUriFromGalleryUri(uri: Uri, folderName: String, isEdit: Boolean = false): ImageData {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        val folder = if (isEdit) {
            getTempFolderName(folderName)
        } else {
            folderName
        }

        Timber.d("getFileUrisFromGalleryUri, $uri")
        val newFile = createFileLocally(uri, folder)
        val newUri = getFileUri(newFile)
        val fileSize = getUriFileSize(newUri)
        val mimeType = getMimeType(uri)
        return ImageData(
            uri = newUri,
            name = newFile.name,
            size = fileSize,
            mimeType = mimeType,
            md5 = hashUtils.md5(newFile),
            isEdit = isEdit
        )
    }

    fun getFileDataFromCameraPicture(
        cameraTakePictureData: CameraTakePictureData,
        isEdit: Boolean
    ): ImageData {
        val uri = cameraTakePictureData.uri
        val file = cameraTakePictureData.file
        Timber.d("getFileUrisFromUri, $cameraTakePictureData")
        val fileSize = getUriFileSize(uri)
        val mimeType = getMimeType(uri)
        return ImageData(
            uri = uri,
            name = getUriFileName(uri),
            size = fileSize,
            mimeType = mimeType,
            md5 = hashUtils.md5(file),
            isEdit = isEdit
        )
    }

    private suspend fun moveFile(sourceFilePath: String, destinationFilePath: String) {
        copyFile(sourceFilePath, destinationFilePath)
        File(sourceFilePath).delete()
    }

    private suspend fun copyFile(sourceFilePath: String, destinationFilePath: String) =
        withContext(ioDispatcher) {
            val sourceFile = File(sourceFilePath)
            val destinationFile = File(destinationFilePath)
            sourceFile.inputStream().use { inputStream ->
                destinationFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

    suspend fun moveSourceFilesToDestinationFolder(
        sourceFolderName: String,
        destinationFolderName: String
    ) {
        Timber.d(
            "moveSourceFilesToDestinationFolder " +
                "source: $sourceFolderName, " +
                "destination: $destinationFolderName"
        )
        processFilesInFolder(sourceFolderName, destinationFolderName) { file, destPath ->
            moveFile(file.path, destPath)
            file.deleteRecursively()
        }
        withContext(ioDispatcher) {
            getMainFolder(sourceFolderName).deleteRecursively()
        }
    }

    suspend fun copySourceFilesToDestination(
        sourceFolderName: String,
        destinationFolderName: String
    ) {
        Timber.d(
            "copySourceFilesToDestination " +
                "sourceFolderName: $sourceFolderName, " +
                "destinationFolderName: $destinationFolderName"
        )
        processFilesInFolder(
            sourceFolderName = sourceFolderName,
            destinationFolderName = destinationFolderName
        ) { file, destPath ->
            copyFile(file.path, destPath)
        }
    }

    private suspend fun processFilesInFolder(
        sourceFolderName: String,
        destinationFolderName: String,
        fileAction: suspend (File, String) -> Unit
    ) = withContext(ioDispatcher) {
        val sourceFolder = getMainFolder(sourceFolderName)
        val destinationFolder = getMainFolder(destinationFolderName)

        val files = sourceFolder.listFiles()
        files?.forEach { file ->
            if (file.isFile) {
                val destinationFilePath = "${destinationFolder.path}/${file.name}"
                try {
                    fileAction(file, destinationFilePath)
                } catch (e: IOException) {
                    Timber.e(e)
                }
            }
        }
    }

    fun getFileUriForTakePicture(
        folderName: String,
        isEdit: Boolean = false
    ): CameraTakePictureData {
        val folder = if (isEdit) {
            getTempFolderName(folderName)
        } else {
            folderName
        }

        val fileName = getBitmapFileName()
        val file = File(getMainFolder(folder), fileName)
        val uri = getFileUri(file)
        return CameraTakePictureData(
            uri = uri,
            file = file
        )
    }

    suspend fun deleteFolder(folderName: String) {
        val file = getMainFolder(folderName)
        withContext(ioDispatcher) {
            file.deleteRecursively()
        }
    }

    fun deleteFile(uriString: String): Boolean {
        val uri = uriParser.parse(uriString)
        return deleteFile(uri)
    }

    fun deleteFile(uri: Uri): Boolean {
        val deletedRows = context.contentResolver.delete(uri, null, null)
        return deletedRows > 0
    }

    suspend fun clearMainFolder(): Boolean = withContext(ioDispatcher) {
        getMainFolder().deleteRecursively()
    }

    /**
     * /data/data/com.grappim.hateitorrateit/files/products/1_2024-01-23_20-04-41/
     */
    fun getMainFolder(productFolder: String = ""): File {
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
    fun getTempFolderName(folder: String): String = "${folder}_temp"

    /**
     * /data/data/com.grappim.hateitorrateit/files/products/1_2024-01-23_20-04-41_backup/
     */
    fun getBackupFolderName(folder: String): String = "${folder}_backup"

    private fun getUriFileName(uri: Uri): String {
        val returnCursor = context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )
        return if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            var name: String
            returnCursor.use {
                returnCursor.moveToFirst()
                name = returnCursor.getString(nameIndex)
            }
            name
        } else {
            val uriPath = requireNotNull(uri.path)
            val file = File(uriPath)
            file.name
        }
    }

    private fun createFileLocally(uri: Uri, folderName: String): File {
        val extension = getUriFileExtension(uri)
        val localFile = File(getMainFolder(folderName), getFileName(extension))
        writeDataToFile(uri, localFile)
        Timber.d("createFileLocally, $localFile")
        return localFile
    }

    /**
     * 2024-01-23_21-04-46_1706040286629.jpg
     */
    private fun getFileName(extension: String): String {
        val date = dateTimeUtils.formatToDocumentFolder(OffsetDateTime.now())
        val millis = dateTimeUtils.getInstantNow().toEpochMilli()
        return "${date}_$millis.$extension"
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

    private fun getBitmapFileName(): String = getFileName("jpg")

    private fun getUriFileExtension(uri: Uri): String {
        val mimeType = getMimeType(uri)
        return MimeTypes.formatMimeType(mimeType)
    }

    private fun getMimeType(uri: Uri): String {
        val uriPath = requireNotNull(uri.path)
        return context.contentResolver.getType(uri)
            ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(File(uriPath).extension)
            ?: error("Cannot get mimeType from $uri")
    }

    private fun getFileUri(file: File): Uri {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        Timber.d("getFileUri from FileProvider: $uri")
        return uri
    }

    private fun getUriFileSize(uri: Uri): Long {
        val returnCursor = context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.SIZE),
            null,
            null,
            null
        )
        return if (returnCursor != null) {
            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
            var size: Long
            returnCursor.use {
                returnCursor.moveToFirst()
                size = returnCursor.getString(sizeIndex).toLong()
            }
            size
        } else {
            val uriPath = requireNotNull(uri.path)
            val file = File(uriPath)
            file.length()
        }
    }
}
