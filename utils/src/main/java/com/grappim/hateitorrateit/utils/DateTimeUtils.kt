package com.grappim.hateitorrateit.utils

import java.time.Instant
import java.time.OffsetDateTime

interface DateTimeUtils {
    fun formatToStoreInDb(offsetDateTime: OffsetDateTime): String

    fun parseFromStoringInDb(string: String): OffsetDateTime

    fun formatToDemonstrate(
        offsetDateTime: OffsetDateTime,
    ): String

    fun formatToDocumentFolder(offsetDateTime: OffsetDateTime): String

    fun getDateTimeUTCNow(): OffsetDateTime

    fun getInstantNow(): Instant
}
