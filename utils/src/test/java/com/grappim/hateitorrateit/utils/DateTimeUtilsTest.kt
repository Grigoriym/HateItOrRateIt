package com.grappim.hateitorrateit.utils

import com.grappim.hateitorrateit.utils.di.DateTimeModule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

private val UTC_ZONE = ZoneId.from(ZoneOffset.UTC)

class DateTimeUtilsTest {

    private val dtfToStore: DateTimeFormatter = mockk()
    private val dtfToDemonstrate: DateTimeFormatter = mockk()
    private val dtfDocumentFolder: DateTimeFormatter = mockk()

    private lateinit var dateTimeUtilsWithMocks: DateTimeUtils
    private lateinit var dateTimeUtilsWithFakes: DateTimeUtils

    @Before
    fun setUp() {
        dateTimeUtilsWithMocks = DateTimeUtils(
            dtfToStore,
            dtfToDemonstrate,
            dtfDocumentFolder
        )

        dateTimeUtilsWithFakes = DateTimeUtils(
            DateTimeModule.provideDtfToStore(),
            DateTimeModule.provideDtfToDemonstrate(),
            DateTimeModule.provideDtfDocumentFolder()
        )
    }

    @Test
    fun `formatToStoreInDb should return correct string with fake`() {
        val dateNow = OffsetDateTime.now()

        val result = dateTimeUtilsWithFakes.formatToStoreInDb(dateNow)

        val expected = DateTimeModule.provideDtfToStore().format(dateNow)

        assertEquals(expected, result)
    }

    @Test
    fun `formatToStoreInDb should return correct string with mock`() {
        val dateNow = OffsetDateTime.now()

        every { dtfToStore.format(any()) } returns "2011-12-03T10:15:30"

        val result = dateTimeUtilsWithMocks.formatToStoreInDb(dateNow)

        verify { dtfToStore.format(any()) }

        assertEquals("2011-12-03T10:15:30", result)
    }

    @Test
    fun `parseFromStoringInDb should return correct string with mock`() {
        val dateNow = OffsetDateTime.now()

        every { dtfToStore.parse(any()) } returns dateNow

        val result = dateTimeUtilsWithMocks.parseFromStoringInDb("2023-11-23T14:16:37.502Z")

        assertEquals(dateNow, result)
    }

    @Test
    fun `parseFromStoringInDb should return correct string with fake`() {
        val dateString = "2023-11-23T14:16:37.502Z"

        val result = dateTimeUtilsWithFakes.parseFromStoringInDb(dateString)

        val expected = OffsetDateTime.from(DateTimeModule.provideDtfToStore().parse(dateString))

        assertEquals(expected, result)
    }

    @Test
    fun `formatToDemonstrate should return correct string with mock`() {
        val dateNow = OffsetDateTime.now()

        every { dtfToDemonstrate.format(any()) } returns "2011-12-03T10:15:30"

        val result = dateTimeUtilsWithMocks.formatToDemonstrate(dateNow)

        verify { dtfToDemonstrate.format(any()) }

        assertEquals("2011-12-03T10:15:30", result)
    }

    @Test
    fun `formatToDemonstrate should return correct string with fake`() {
        val dateNow = OffsetDateTime.now()

        val result = dateTimeUtilsWithFakes.formatToDemonstrate(dateNow)

        val expected = DateTimeModule.provideDtfToDemonstrate().format(dateNow)

        assertEquals(expected, result)
    }

    @Test
    fun `formatToDocumentFolder should return correct string with mock`() {
        val dateNow = OffsetDateTime.now()

        every { dtfDocumentFolder.format(any()) } returns "2011-12-03T10:15:30"

        val result = dateTimeUtilsWithMocks.formatToDocumentFolder(dateNow)

        verify { dtfDocumentFolder.format(any()) }

        assertEquals("2011-12-03T10:15:30", result)
    }

    @Test
    fun `formatToDocumentFolder should return correct string with fake`() {
        val dateNow = OffsetDateTime.now()

        val result = dateTimeUtilsWithFakes.formatToDocumentFolder(dateNow)

        val expected = DateTimeModule.provideDtfDocumentFolder().format(dateNow)

        assertEquals(expected, result)
    }

    @Test
    fun `getDateTimeUTCNow should return correct string`() {
        val dateNow = OffsetDateTime.now(ZoneOffset.UTC)

        val result = dateTimeUtilsWithMocks.getDateTimeUTCNow()

        assertEquals(dateNow.withOffsetSameInstant(ZoneOffset.UTC), result)
    }
}
