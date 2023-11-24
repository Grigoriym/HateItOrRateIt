package com.grappim.hateitorrateit.domain

import java.time.OffsetDateTime

data class DraftProduct(
    val id: Long,
    val date: OffsetDateTime,
    val folderName: String,
    val type: HateRateType,
)
