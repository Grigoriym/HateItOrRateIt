package com.grappim.hateitorrateit.utils

import com.grappim.domain.HateRateType
import java.time.OffsetDateTime

data class DraftProduct(
    val id: Long,
    val date: OffsetDateTime,
    val folderName: String,
    val type: HateRateType,
)
