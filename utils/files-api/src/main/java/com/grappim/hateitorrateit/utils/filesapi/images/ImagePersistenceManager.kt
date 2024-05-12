package com.grappim.hateitorrateit.utils.filesapi.images

import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.utils.filesapi.models.ImageData

interface ImagePersistenceManager {
    suspend fun prepareEditedImagesToPersist(images: List<ImageData>): List<ProductImageData>
}
