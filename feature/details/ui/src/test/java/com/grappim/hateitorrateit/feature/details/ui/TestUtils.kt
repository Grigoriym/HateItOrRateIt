package com.grappim.hateitorrateit.feature.details.ui

import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.feature.details.ui.model.ProductDetailsImageUi
import com.grappim.hateitorrateit.feature.details.ui.model.ProductDetailsUi
import com.grappim.hateitorrateit.testing.domain.getImages

const val DATE = "123,231.23"

fun createProductDetailsUi() = ProductDetailsUi(
    id = "1",
    name = "Test Product",
    createdDate = DATE,
    productFolderName = "folderName",
    shop = "Shop",
    description = "Description",
    images = getImages(),
    type = HateRateType.HATE
)

fun createProductDetailsImageUi() = ProductDetailsImageUi(
    images = getImages()
)
