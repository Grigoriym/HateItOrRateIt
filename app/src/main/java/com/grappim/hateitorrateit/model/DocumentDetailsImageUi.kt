package com.grappim.hateitorrateit.model

import com.grappim.domain.DocumentFileData

data class DocumentDetailsImageUi(
    val filesUri: List<DocumentFileData> = emptyList(),
)