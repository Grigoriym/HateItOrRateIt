package com.grappim.hateitorrateit.utils.file

import android.net.Uri
import com.grappim.hateitorrateit.utils.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.models.ImageData

interface FileUriManager {
    fun getFileUriFromGalleryUri(uri: Uri, folderName: String, isEdit: Boolean = false): ImageData

    fun getFileDataFromCameraPicture(
        cameraTakePictureData: CameraTakePictureData,
        isEdit: Boolean
    ): ImageData

    fun getFileUriForTakePicture(folderName: String, isEdit: Boolean = false): CameraTakePictureData
}
