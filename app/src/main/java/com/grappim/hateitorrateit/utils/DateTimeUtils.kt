package com.grappim.hateitorrateit.utils

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateTimeUtils @Inject constructor(
    @DtfToStore private val dtfToStore: DateTimeFormatter,
    @DtfToDemonstrate private val dtfToDemonstrate: DateTimeFormatter,
    @DtfGDriveDocumentFolder private val dtfGDriveDocumentFolder: DateTimeFormatter,
) {

    fun formatToStore(offsetDateTime: OffsetDateTime): String =
        dtfToStore.format(offsetDateTime)

    fun parseToStore(string: String): OffsetDateTime =
        OffsetDateTime.from(dtfToStore.parse(string))

    fun formatToDemonstrate(
        offsetDateTime: OffsetDateTime,
        inUtc: Boolean = false
    ): String =
        if (inUtc) {
            val createdDateInCurrentOffset = getLocalTimeFromUTC(offsetDateTime)
            dtfToDemonstrate.format(createdDateInCurrentOffset)
        } else {
            dtfToDemonstrate.format(offsetDateTime)
        }

    fun formatToGDrive(offsetDateTime: OffsetDateTime): String =
        dtfGDriveDocumentFolder.format(offsetDateTime)

    fun getDateTimeUTCNow(): OffsetDateTime =
        OffsetDateTime.now(ZoneOffset.UTC)

    fun getLocalTimeFromUTC(offsetDateTime: OffsetDateTime): OffsetDateTime =
        offsetDateTime
            .withOffsetSameInstant(
                OffsetDateTime.now().offset
            )

}
