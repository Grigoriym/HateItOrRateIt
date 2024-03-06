package com.grappim.hateitorrateit.utils.file.images

import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.utils.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.models.ImageData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagePersistenceManagerImpl @Inject constructor(
    private val imageDataMapper: ImageDataMapper
) : ImagePersistenceManager {

    /**
     * Prepares a list of edited images for persistence by updating their URI paths.
     *
     * This function iterates through a list of [ImageData] objects and checks for images marked as edited.
     * For each edited image, it removes the "_temp" suffix from its URI path and URI string,
     * indicating that the image is ready to be moved from a temporary editing location to a permanent storage location.
     * Images not marked as edited are left unchanged.
     *
     * @param images A list of [ImageData] objects to be processed.
     * @return A list of [ProductImageData] objects with updated URI paths for edited images.
     */
    override suspend fun prepareEditedImagesToPersist(
        images: List<ImageData>
    ): List<ProductImageData> = images.map { image ->
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
