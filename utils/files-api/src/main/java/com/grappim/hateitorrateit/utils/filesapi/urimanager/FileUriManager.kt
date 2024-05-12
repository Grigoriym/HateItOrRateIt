package com.grappim.hateitorrateit.utils.filesapi.urimanager

import android.net.Uri
import com.grappim.hateitorrateit.utils.filesapi.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.filesapi.models.ImageData

interface FileUriManager {
    suspend fun getFileUriFromGalleryUri(
        uri: Uri,
        folderName: String,
        isEdit: Boolean = false
    ): ImageData

    fun getFileDataFromCameraPicture(
        cameraTakePictureData: CameraTakePictureData,
        isEdit: Boolean
    ): ImageData

    fun getFileUriForTakePicture(folderName: String, isEdit: Boolean = false): CameraTakePictureData
}
