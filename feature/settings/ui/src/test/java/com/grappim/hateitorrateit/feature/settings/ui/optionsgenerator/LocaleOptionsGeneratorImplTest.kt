package com.grappim.hateitorrateit.feature.settings.ui.optionsgenerator

import com.grappim.hateitorrateit.uikit.R
import com.grappim.hateitorrateit.utils.ui.NativeText
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
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
        assertEquals("en", localeOptions[NativeText.Resource(R.string.en)])
        assertEquals("fr", localeOptions[NativeText.Resource(R.string.fr)])
        assertEquals("de", localeOptions[NativeText.Resource(R.string.de)])
    }
}
