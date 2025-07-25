package com.grappim.hateitorrateit.utils.androidimpl

import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.grappim.hateitorrateit.core.async.IoDispatcher
import com.grappim.hateitorrateit.core.async.resultOf
import com.grappim.hateitorrateit.utils.androidapi.GalleryInteractions
import com.grappim.hateitorrateit.utils.filesapi.inforetriever.FileInfoRetriever
import com.grappim.hateitorrateit.utils.filesapi.transfer.FileTransferOperations
import com.grappim.hateitorrateit.utils.filesapi.uri.UriParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class GalleryInteractionsImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val fileInfoRetriever: FileInfoRetriever,
    private val uriParser: UriParser,
    private val fileTransferOperations: FileTransferOperations
) : GalleryInteractions {

    companion object {
        const val GALLERY_FOLDER_NAME = "HateItOrRateIt"
    }

    /**
     * @param uriString is the uri of the sourceFile
     * @param name is the name of the sourceFile
     * @param mimeType is the mimeType of the sourceFile
     * @param folderName is the name of the folder of the sourceFile
     */
    override suspend fun saveImageInGallery(
        uriString: String,
        name: String,
        mimeType: String,
        folderName: String
    ): Result<Unit> = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        saveImageApi28(name, folderName)
    } else {
        saveImage(uriString, name, mimeType)
    }

    private suspend fun saveImageApi28(name: String, folderName: String): Result<Unit> = resultOf {
        withContext(ioDispatcher) {
            val sourceFile = fileInfoRetriever.findFileInFolder(
                fileName = name,
                folderName = folderName
            )

            val picturesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val targetDirFile = File(picturesDir, GALLERY_FOLDER_NAME)
            if (targetDirFile.exists().not()) {
                targetDirFile.mkdirs()
            }
            val targetFile = File(targetDirFile, name)

            if (targetFile.exists().not()) {
                targetFile.mkdirs()
            }
            fileTransferOperations.writeSourceFileToTargetFile(sourceFile, targetFile)
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private suspend fun saveImage(uriString: String, name: String, mimeType: String): Result<Unit> =
        resultOf {
            withContext(ioDispatcher) {
                val imagesCollection =
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val imageFileUri = uriParser.parse(uriString)
                val destDir = File(Environment.DIRECTORY_PICTURES, GALLERY_FOLDER_NAME)
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, name)
                    put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                    put(MediaStore.Images.Media.RELATIVE_PATH, "$destDir${File.separator}")
                }
                val contentResolver = context.contentResolver
                val newImageUri = contentResolver
                    .insert(imagesCollection, contentValues)
                    ?: error("Error retrieving image uri")
                val inputStream = contentResolver.openInputStream(imageFileUri)
                    ?: error("Error retrieving input stream")
                val outputStream = contentResolver.openOutputStream(newImageUri)
                    ?: error("Error retrieving output stream")
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
                Timber.d("newImageUri: $newImageUri")
            }
        }
}
