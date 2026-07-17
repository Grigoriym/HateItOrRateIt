package com.grappim.hateitorrateit.feature.settings.ui.optionsgenerator

import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.utils.ui.NativeText
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class LocaleOptionsGeneratorImplTest {

    private lateinit var localeOptionsGenerator: LocaleOptionsGeneratorImpl

    @Before
    fun setUp() {
        localeOptionsGenerator = LocaleOptionsGeneratorImpl()
    }

    @Test
    fun getLocaleOptions() {
        val localeOptions = localeOptionsGenerator.getLocaleOptions()

        assertEquals(3, localeOptions.size)
        assertEquals("en", localeOptions[NativeText.Resource(RString.en)])
        assertEquals("fr", localeOptions[NativeText.Resource(RString.fr)])
        assertEquals("de", localeOptions[NativeText.Resource(RString.de)])
    }
}
