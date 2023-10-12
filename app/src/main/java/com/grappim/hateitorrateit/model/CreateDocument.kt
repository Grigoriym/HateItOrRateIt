package com.grappim.hateitorrateit.model

import com.grappim.hateitorrateit.domain.DocumentFileData
import java.time.OffsetDateTime

data class CreateDocument(
    val id: Long,
    val name: String,
    val filesUri: List<DocumentFileData>,
    val createdDate: OffsetDateTime,
    val documentFolderName: String,
    val description: String,
    val shop: String,
)