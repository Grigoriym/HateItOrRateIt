package com.grappim.hateitorrateit.di

import com.grappim.hateitorrateit.BuildConfig
import com.grappim.hateitorrateit.commons.AppInfoData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class AppInfoModule {
    @[Provides Singleton]
    fun provideAppInfoData(): AppInfoData = AppInfoData(
        isDebug = BuildConfig.DEBUG
    )
}
