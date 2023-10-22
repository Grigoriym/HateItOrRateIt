package com.grappim.hateitorrateit.utils

import com.grappim.domain.HateRateType
import java.time.OffsetDateTime

data class DraftDocument(
    val id: Long,
    val date: OffsetDateTime,
    val folderName: String,
    val type: HateRateType,
)
