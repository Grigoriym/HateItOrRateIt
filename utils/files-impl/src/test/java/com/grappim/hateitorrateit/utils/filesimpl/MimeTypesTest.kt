package com.grappim.hateitorrateit.utils.filesimpl

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MimeTypesTest {

    private lateinit var mimeTypes: MimeTypes

    @Before
    fun setup() {
        mimeTypes = MimeTypes()
    }

    @Test
    fun `on formatMimeType with png should return correct type`() {
        val mimeType = "image/png"
        val actual = mimeTypes.formatMimeType(mimeType)

        assertEquals(actual, "png")
    }

    @Test
    fun `on formatMimeType with jpg should return correct type`() {
        val mimeType = "image/jpeg"
        val actual = mimeTypes.formatMimeType(mimeType)

        assertEquals(actual, "jpg")
    }

    @Test
    fun `on formatMimeType with incorrect type should return unknown`() {
        val mimeType = "image/pof"
        val actual = mimeTypes.formatMimeType(mimeType)

        assertEquals(actual, "unknown")
    }
}
