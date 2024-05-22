package com.grappim.hateitorrateit.utils.filesapi.images

import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import com.grappim.hateitorrateit.utils.filesapi.models.ProductImageUIData

interface ImagePersistenceManager {
    suspend fun prepareEditedImagesToPersist(images: List<ProductImageUIData>): List<ProductImage>
}
