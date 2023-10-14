package com.grappim.hateitorrateit.domain

import com.grappim.hateitorrateit.core.HateRateType
import java.time.OffsetDateTime

data class Document(
    val id: Long,
    val name: String,
    val filesUri: List<DocumentFileData>,
    val createdDate: OffsetDateTime,
    val documentFolderName: String,
    val description: String,
    val shop: String,
    val type: HateRateType,
)
