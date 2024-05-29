package com.grappim.hateitorrateit.utils.androidapi

interface GalleryInteractions {
    suspend fun saveImageInGallery(
        uriString: String,
        name: String,
        mimeType: String,
        folderName: String
    ): SaveImageState
}

sealed interface SaveImageState {
    data object Initial : SaveImageState
    data object Success : SaveImageState
    data object Failure : SaveImageState
}
