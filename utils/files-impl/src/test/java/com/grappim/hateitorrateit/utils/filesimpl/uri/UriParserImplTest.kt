package com.grappim.hateitorrateit.utils.filesimpl.uri

import android.net.Uri
import com.grappim.hateitorrateit.utils.filesapi.uri.UriParser
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class UriParserImplTest {
    private lateinit var uriParser: UriParser

    @Before
    fun setup() {
        uriParser = UriParserImpl()
    }

    @Test
    fun `on uri string parse should return correct uri`() {
        val uriString = "testUri"

        val expected = Uri.parse(uriString)

        val actual = uriParser.parse(uriString)

        assertEquals(expected, actual)
    }
}
