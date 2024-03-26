package com.grappim.hateitorrateit.di

import com.grappim.hateitorrateit.ui.screens.settings.LocaleOptionsGenerator
import com.grappim.hateitorrateit.ui.screens.settings.LocaleOptionsGeneratorImpl
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
