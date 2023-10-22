package com.grappim.hateitorrateit.ui.screens.details.docimage

import com.grappim.domain.DocumentFileData

data class DocImageViewModelState(
    val uri: String = "",
    val fileUris: List<DocumentFileData> = emptyList(),
)
