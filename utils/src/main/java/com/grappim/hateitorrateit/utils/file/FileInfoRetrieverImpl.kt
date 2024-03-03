package com.grappim.hateitorrateit.utils.file

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.grappim.hateitorrateit.utils.DateTimeUtils
import com.grappim.hateitorrateit.utils.MimeTypes
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileInfoRetrieverImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mimeTypes: MimeTypes,
    private val dateTimeUtils: DateTimeUtils
) : FileInfoRetriever {

    override fun getFileExtension(uri: Uri): String {
        val mimeType = getMimeType(uri)
        return mimeTypes.formatMimeType(mimeType)
    }

    override fun getMimeType(uri: Uri): String {
        val uriPath = requireNotNull(uri.path)
        return context.contentResolver.getType(uri)
            ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(File(uriPath).extension)
            ?: error("Cannot get mimeType from $uri")
    }

    override fun getFileSize(uri: Uri): Long {
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

    override fun getFileName(uri: Uri): String {
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

    /**
     * 2024-01-23_21-04-46_1706040286629.jpg
     */
    override fun getFileName(extension: String): String {
        val date = dateTimeUtils.formatToDocumentFolder(OffsetDateTime.now())
        val millis = dateTimeUtils.getInstantNow().toEpochMilli()
        return "${date}_$millis.$extension"
    }

    override fun getBitmapFileName(): String = getFileName("jpg")
}
