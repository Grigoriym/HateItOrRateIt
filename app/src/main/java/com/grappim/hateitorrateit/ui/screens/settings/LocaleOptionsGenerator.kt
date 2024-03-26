package com.grappim.hateitorrateit.ui.screens.settings

import com.grappim.hateitorrateit.core.NativeText

interface LocaleOptionsGenerator {
    fun getLocaleOptions(): Map<NativeText, String>
}
