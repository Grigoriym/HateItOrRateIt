package com.grappim.hateitorrateit.feature.settings.ui.optionsgenerator

import com.grappim.hateitorrateit.utils.ui.NativeText

interface LocaleOptionsGenerator {
    fun getLocaleOptions(): Map<NativeText, String>
}
