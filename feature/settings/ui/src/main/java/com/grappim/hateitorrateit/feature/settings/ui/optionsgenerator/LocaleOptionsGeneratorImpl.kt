package com.grappim.hateitorrateit.feature.settings.ui.optionsgenerator

import com.grappim.hateitorrateit.feature.settings.ui.R
import com.grappim.hateitorrateit.utils.ui.NativeText
import javax.inject.Inject

class LocaleOptionsGeneratorImpl @Inject constructor() : LocaleOptionsGenerator {
    override fun getLocaleOptions(): Map<NativeText, String> = mapOf(
        NativeText.Resource(R.string.en) to "en",
        NativeText.Resource(R.string.fr) to "fr",
        NativeText.Resource(R.string.de) to "de"
    )
}
