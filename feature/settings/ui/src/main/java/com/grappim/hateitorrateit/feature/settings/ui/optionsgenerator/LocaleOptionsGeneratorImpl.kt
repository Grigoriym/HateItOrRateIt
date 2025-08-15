package com.grappim.hateitorrateit.feature.settings.ui.optionsgenerator

import com.grappim.hateitorrateit.feature.settings.ui.R
import com.grappim.hateitorrateit.utils.ui.NativeText
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import javax.inject.Inject

class LocaleOptionsGeneratorImpl @Inject constructor() : LocaleOptionsGenerator {
    override fun getLocaleOptions(): ImmutableMap<NativeText, String> = persistentMapOf(
        NativeText.Resource(R.string.en) to "en",
        NativeText.Resource(R.string.fr) to "fr",
        NativeText.Resource(R.string.de) to "de"
    )
}
