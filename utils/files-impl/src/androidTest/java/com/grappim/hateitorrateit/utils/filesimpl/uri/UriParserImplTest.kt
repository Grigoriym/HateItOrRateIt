package com.grappim.hateitorrateit.utils.filesimpl.uri

import android.net.Uri
import com.grappim.hateitorrateit.utils.filesapi.uri.UriParser
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class UriParserImplTest {
    private lateinit var uriParser: UriParser

    @Before
    fun setup() {
        uriParser = UriParserImpl()
    }

    @Test
    fun on_uri_string_parse_should_return_correct_uri() {
        val uriString = "testUri"

        val expected = Uri.parse(uriString)

        val actual = uriParser.parse(uriString)

        assertEquals(expected, actual)
    }
}
