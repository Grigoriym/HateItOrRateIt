package com.grappim.hateitorrateit.utils.ui

import com.grappim.hateitorrateit.testing.core.FakeContext
import org.junit.Test
import kotlin.test.assertEquals

class NativeTextTest {

    private val context = FakeContext()

    @Test
    fun `empty NativeText should return empty string`() {
        val empty = NativeText.Empty

        assertEquals(empty.asString(context), "")
    }

    @Test
    fun `simple NativeText should return provided text`() {
        val text = "some text to check"
        val simple = NativeText.Simple(text)

        assertEquals(simple.asString(context), text)
    }

    @Test
    fun `multi NativeText should return correct text`() {
        val multi = NativeText.Multi(
            listOf(
                NativeText.Simple("simple"),
                NativeText.Empty,
                NativeText.Simple("text")
            )
        )

        assertEquals(multi.asString(context), "simpletext")
    }
}
