package com.grappim.hateitorrateit.data.db.converters

import com.grappim.hateitorrateit.datetime.DateTimeUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class DateTimeConverterTest {

    private val dateTimeUtils: DateTimeUtils = mockk()

    private lateinit var converter: DateTimeConverter

    @Before
    fun setUp() {
        converter = DateTimeConverter(dateTimeUtils)
    }

    @Test
    fun fromDateTime() {
        val date = OffsetDateTime.now()
        val expected = "897 87 87"

        every { dateTimeUtils.formatToStoreInDb(any()) } returns expected

        val actual = converter.fromDateTime(date)

        assertEquals(actual, expected)

        verify { dateTimeUtils.formatToStoreInDb(date) }
    }

    @Test
    fun toDateTime() {
        val string = "123 3434"
        val expected = OffsetDateTime.now()

        every { dateTimeUtils.parseFromStoringInDb(any()) } returns expected

        val actual = converter.toDateTime(string)

        assertEquals(actual, expected)

        verify { dateTimeUtils.parseFromStoringInDb(string) }
    }
}
