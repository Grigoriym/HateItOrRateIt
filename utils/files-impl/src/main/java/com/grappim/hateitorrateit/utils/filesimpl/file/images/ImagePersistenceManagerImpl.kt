package com.grappim.hateitorrateit.utils.filesimpl.file.images

import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import com.grappim.hateitorrateit.utils.filesapi.images.ImagePersistenceManager
import com.grappim.hateitorrateit.utils.filesapi.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.filesapi.models.ProductImageUIData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagePersistenceManagerImpl @Inject constructor(
    private val imageDataMapper: ImageDataMapper
) : ImagePersistenceManager {

    /**
     * Prepares a list of edited images for persistence by updating their URI paths.
     *
     * This function iterates through a list of [ProductImageUIData] objects and checks for images marked as edited.
     * For each edited image, it removes the "_temp" suffix from its URI path and URI string,
     * indicating that the image is ready to be moved from a temporary editing location to a permanent storage location.
     * Images not marked as edited are left unchanged.
     *
     * @param images A list of [ProductImageUIData] objects to be processed.
     * @return A list of [ProductImage] objects with updated URI paths for edited images.
     */
    override suspend fun prepareEditedImagesToPersist(
        images: List<ProductImageUIData>
    ): List<com.grappim.hateitorrateit.data.repoapi.models.ProductImage> = images.map { image ->
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
}
