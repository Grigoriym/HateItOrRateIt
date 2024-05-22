package com.grappim.hateitorrateit.data.db.converters

import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HateRateTypeConverterTest {

    private lateinit var converter: HateRateTypeConverter

    @Before
    fun setUp() {
        converter = HateRateTypeConverter()
    }

    @Test
    fun `when toHateRate should return correct type`() {
        val type = "RATE"
        val actual = converter.toHateRate(type)
        assertEquals(actual, com.grappim.hateitorrateit.data.repoapi.models.HateRateType.RATE)
    }

    @Test
    fun `when fromHateRate should return correct String`() {
        val type = com.grappim.hateitorrateit.data.repoapi.models.HateRateType.HATE
        val actual = converter.fromHateRate(type)
        assertEquals(actual, "HATE")
    }
}
