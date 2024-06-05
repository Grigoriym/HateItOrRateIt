package com.grappim.hateitorrateit.datetime

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

class DateTimeUtilsTest {

    private val dtfToStore: DateTimeFormatter = mockk()
    private val dtfToDemonstrate: DateTimeFormatter = mockk()
    private val dtfDocumentFolder: DateTimeFormatter = mockk()

    private lateinit var sut: DateTimeUtilsImpl

    @Before
    fun setUp() {
        sut = DateTimeUtilsImpl(
            dtfToStore,
            dtfToDemonstrate,
            dtfDocumentFolder
        )
    }

    @Test
    fun `formatToStoreInDb should return correct string with mock`() {
        val dateNow = OffsetDateTime.now()

        every { dtfToStore.format(any()) } returns "2011-12-03T10:15:30"

        val result = sut.formatToStoreInDb(dateNow)

        verify { dtfToStore.format(any()) }

        assertEquals("2011-12-03T10:15:30", result)
    }

    @Test
    fun `parseFromStoringInDb should return correct string with mock`() {
        val dateNow = OffsetDateTime.now()

        every { dtfToStore.parse(any()) } returns dateNow

        val result = sut.parseFromStoringInDb("2023-11-23T14:16:37.502Z")

        assertEquals(dateNow, result)
    }

    @Test
    fun `formatToDemonstrate should return correct string with mock`() {
        val dateNow = OffsetDateTime.now()

        every { dtfToDemonstrate.format(any()) } returns "2011-12-03T10:15:30"

        val result = sut.formatToDemonstrate(dateNow)

        verify { dtfToDemonstrate.format(any()) }

        assertEquals("2011-12-03T10:15:30", result)
    }

    @Test
    fun `formatToDocumentFolder should return correct string with mock`() {
        val dateNow = OffsetDateTime.now()

        every { dtfDocumentFolder.format(any()) } returns "2011-12-03T10:15:30"

        val result = sut.formatToDocumentFolder(dateNow)

        verify { dtfDocumentFolder.format(any()) }

        assertEquals("2011-12-03T10:15:30", result)
    }
}
