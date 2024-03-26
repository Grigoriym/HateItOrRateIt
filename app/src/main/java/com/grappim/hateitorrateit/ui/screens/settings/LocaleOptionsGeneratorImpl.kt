package com.grappim.hateitorrateit.ui.screens.settings

import com.grappim.hateitorrateit.core.NativeText
import com.grappim.hateitorrateit.ui.R
import javax.inject.Inject

class LocaleOptionsGeneratorImpl @Inject constructor() : LocaleOptionsGenerator {
    override fun getLocaleOptions(): Map<NativeText, String> = mapOf(
        NativeText.Resource(R.string.en) to "en",
        NativeText.Resource(R.string.fr) to "fr",
        NativeText.Resource(R.string.de) to "de"
    )
}
