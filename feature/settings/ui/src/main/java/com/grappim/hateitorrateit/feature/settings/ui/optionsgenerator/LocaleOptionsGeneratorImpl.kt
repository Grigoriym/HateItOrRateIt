package com.grappim.hateitorrateit.feature.settings.ui.optionsgenerator

import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.utils.ui.NativeText
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import javax.inject.Inject

class LocaleOptionsGeneratorImpl @Inject constructor() : LocaleOptionsGenerator {
    override fun getLocaleOptions(): ImmutableMap<NativeText, String> = persistentMapOf(
        NativeText.Resource(RString.en) to "en",
        NativeText.Resource(RString.fr) to "fr",
        NativeText.Resource(RString.de) to "de"
    )
}
