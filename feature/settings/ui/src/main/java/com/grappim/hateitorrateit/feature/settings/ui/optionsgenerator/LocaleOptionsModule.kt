package com.grappim.hateitorrateit.feature.settings.ui.optionsgenerator

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface LocaleOptionsModule {

    @Binds
    fun bindLocaleOptionsGenerator(
        localeOptionsGeneratorImpl: LocaleOptionsGeneratorImpl
    ): LocaleOptionsGenerator
}
