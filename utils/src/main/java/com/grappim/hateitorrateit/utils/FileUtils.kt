package com.grappim.hateitorrateit.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.text.format.Formatter.formatShortFileSize
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.utils.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.models.ImageData
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileUtils @Inject constructor(
    @ApplicationContext private val context: Context,
    private val hashUtils: HashUtils,
    private val dateTimeUtils: DateTimeUtils,
) {

    fun toProductImageData(imageData: ImageData): ProductImageData =
        ProductImageData(
            name = imageData.name,
            mimeType = imageData.mimeType,
            uriPath = imageData.uri.path ?: "",
            uriString = imageData.uri.toString(),
            size = imageData.size,
            md5 = imageData.md5,
        )

    fun getFileUrisFromGalleryUri(
        uri: Uri,
        folderName: String,
    ): ImageData {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        Timber.d("getFileUrisFromGalleryUri, $uri")
        val newFile = createFileLocally(uri, folderName)
        val newUri = getFileUri(newFile)
        val fileSize = getUriFileSize(newUri)
        val mimeType = getMimeType(uri)
        return ImageData(
            uri = newUri,
            name = newFile.name,
            size = fileSize,
            mimeType = mimeType,
            md5 = hashUtils.md5(newFile)
        )
    }

    fun getFileUriForTakePicture(
        folderName: String
    ): CameraTakePictureData {
        val fileName = getBitmapFileName()
        val file = File(getMainFolder(folderName), fileName)
        val uri = getFileUri(file)
        return CameraTakePictureData(
            uri = uri,
            file = file
        )
    }

    fun deleteFolder(folderName: String) {
        val file = getMainFolder(folderName)
        file.deleteRecursively()
    }

    fun deleteFile(uriString: String): Boolean {
        val uri = Uri.parse(uriString)
        return deleteFile(uri)
    }

    fun deleteFile(uri: Uri): Boolean {
        val deletedRows = context.contentResolver.delete(uri, null, null)
        return deletedRows > 0
    }

    fun getFileDataFromCameraPicture(
        cameraTakePictureData: CameraTakePictureData
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
            md5 = hashUtils.md5(file)
        )
    }

    private fun getUriFileName(
        uri: Uri
    ): String {
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
            val file = File(uri.path)
            file.name
        }
    }

    private fun formatFileSize(
        fileSize: Long
    ): String = formatShortFileSize(context, fileSize)

    private fun createFileLocally(
        uri: Uri,
        folderName: String
    ): File {
        val extension = getUriFileExtension(uri)
        val localFile = File(getMainFolder(folderName), getFileName(extension))
        writeDataToFile(uri, localFile)
        Timber.d("createFileLocally, $localFile")
        return localFile
    }

    private fun getFileName(
        extension: String
    ): String {
        val date = dateTimeUtils.formatToGDrive(OffsetDateTime.now())
        val millis = Instant.now().toEpochMilli()
        return "${date}_${millis}.$extension"
    }

    private fun writeDataToFile(uri: Uri, newFile: File) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: error("can not create inputStream from $uri")

        inputStream.use { input ->
            val outputStream = FileOutputStream(newFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024)
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
    }

    fun getMainFolder(
        child: String
    ): File {
        val folder = File(context.filesDir, "products/$child")
        if (folder.exists().not()) {
            folder.mkdirs()
        }
        return folder
    }

    private fun getBitmapFileName(
        prefix: String = ""
    ): String =
        StringBuilder()
            .apply {
                if (prefix.isNotEmpty()) {
                    append("${prefix}_")
                }
                append(getFileName("jpg"))
            }
            .toString()

    private fun getUriFileExtension(uri: Uri): String {
        val mimeType = getMimeType(uri)
        return MimeTypes.formatMimeType(mimeType)
    }

    private fun getMimeType(uri: Uri): String =
        context.contentResolver.getType(uri)
            ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(File(uri.path).extension)
            ?: error("Cannot get mimeType from $uri")

    private fun getFileUri(
        file: File
    ): Uri {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        Timber.d("getFileUri from FileProvider: $uri")
        return uri
    }

    private fun getUriFileSize(
        uri: Uri
    ): Long {
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
            val file = File(uri.path)
            file.length()
        }

    }
}
