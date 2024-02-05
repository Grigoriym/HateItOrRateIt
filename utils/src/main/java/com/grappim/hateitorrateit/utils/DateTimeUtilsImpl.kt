package com.grappim.hateitorrateit.utils

import com.grappim.hateitorrateit.utils.di.DtfDocumentFolder
import com.grappim.hateitorrateit.utils.di.DtfToDemonstrate
import com.grappim.hateitorrateit.utils.di.DtfToStore
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateTimeUtilsImpl @Inject constructor(
    @DtfToStore private val dtfToStore: DateTimeFormatter,
    @DtfToDemonstrate private val dtfToDemonstrate: DateTimeFormatter,
    @DtfDocumentFolder private val dtfDocumentFolder: DateTimeFormatter,
) : DateTimeUtils {

    override fun formatToStoreInDb(offsetDateTime: OffsetDateTime): String =
        dtfToStore.format(offsetDateTime)

    override fun parseFromStoringInDb(string: String): OffsetDateTime =
        OffsetDateTime.from(dtfToStore.parse(string))

    override fun formatToDemonstrate(
        offsetDateTime: OffsetDateTime,
    ): String = dtfToDemonstrate.format(offsetDateTime)

    override fun formatToDocumentFolder(offsetDateTime: OffsetDateTime): String =
        dtfDocumentFolder.format(offsetDateTime)

    override fun getDateTimeUTCNow(): OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)

    override fun getInstantNow(): Instant = Instant.now()
}
