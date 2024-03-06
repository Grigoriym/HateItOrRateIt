package com.grappim.hateitorrateit.utils.file.images

import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.utils.models.ImageData

interface ImagePersistenceManager {
    suspend fun prepareEditedImagesToPersist(images: List<ImageData>): List<ProductImageData>
}
