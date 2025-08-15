package com.grappim.hateitorrateit.feature.settings.ui.optionsgenerator

import com.grappim.hateitorrateit.utils.ui.NativeText
import kotlinx.collections.immutable.ImmutableMap

interface LocaleOptionsGenerator {
    fun getLocaleOptions(): ImmutableMap<NativeText, String>
}
