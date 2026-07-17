package com.grappim.hateitorrateit.utils.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import kotlin.test.assertEquals

class NativeTextResourceTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun resource_NativeText_should_return_provided_text() {
        val idRes = R.string.test_string
        val simple = NativeText.Resource(idRes)

        assertEquals(simple.asString(context), context.getString(idRes))
    }
}
