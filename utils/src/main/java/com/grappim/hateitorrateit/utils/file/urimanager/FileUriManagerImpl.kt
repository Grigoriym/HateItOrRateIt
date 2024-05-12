package com.grappim.hateitorrateit.utils.file.urimanager

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.grappim.hateitorrateit.utils.HashUtils
import com.grappim.hateitorrateit.utils.file.creation.FileCreationUtils
import com.grappim.hateitorrateit.utils.file.inforetriever.FileInfoRetriever
import com.grappim.hateitorrateit.utils.file.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.models.ImageData
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileUriManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val folderPathManager: FolderPathManager,
    private val hashUtils: HashUtils,
    private val fileInfoRetriever: FileInfoRetriever,
    private val fileCreationUtils: FileCreationUtils
) : FileUriManager {

    override suspend fun getFileUriFromGalleryUri(
        uri: Uri,
        folderName: String,
        isEdit: Boolean
    ): ImageData {
        val folder = if (isEdit) {
            folderPathManager.getTempFolderName(folderName)
        } else {
            folderName
        }

        Timber.d("getFileUrisFromGalleryUri, $uri")
        val newFile = fileCreationUtils.createFileLocally(uri, folder)
        val newUri = getFileUri(newFile)
        val fileSize = fileInfoRetriever.getFileSize(newUri)
        val mimeType = fileInfoRetriever.getMimeType(newUri)
        return ImageData(
            uri = newUri,
            name = newFile.name,
            size = fileSize,
            mimeType = mimeType,
            md5 = hashUtils.md5(newFile),
            isEdit = isEdit
        )
    }

    override fun getFileDataFromCameraPicture(
        cameraTakePictureData: CameraTakePictureData,
        isEdit: Boolean
    ): ImageData {
        val uri = cameraTakePictureData.uri
        val file = cameraTakePictureData.file
        Timber.d("getFileUrisFromUri, $cameraTakePictureData")
        val fileSize = fileInfoRetriever.getFileSize(uri)
        val mimeType = fileInfoRetriever.getMimeType(uri)
        return ImageData(
            uri = uri,
            name = fileInfoRetriever.getFileName(uri),
            size = fileSize,
            mimeType = mimeType,
            md5 = hashUtils.md5(file),
            isEdit = isEdit
        )
    }

    override fun getFileUriForTakePicture(
        folderName: String,
        isEdit: Boolean
    ): CameraTakePictureData {
        val folder = if (isEdit) {
            folderPathManager.getTempFolderName(folderName)
        } else {
            folderName
        }

        val fileName = fileInfoRetriever.getBitmapFileName()
        val file = File(folderPathManager.getMainFolder(folder), fileName)
        val uri = getFileUri(file)
        return CameraTakePictureData(
            uri = uri,
            file = file
        )
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
}
