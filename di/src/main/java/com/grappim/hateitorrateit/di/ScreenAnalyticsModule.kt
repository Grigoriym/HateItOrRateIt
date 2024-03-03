package com.grappim.hateitorrateit.di

import com.grappim.hateitorrateit.analyticsapi.DetailsAnalytics
import com.grappim.hateitorrateit.analyticsapi.HomeAnalytics
import com.grappim.hateitorrateit.analyticsapi.ProductManagerAnalytics
import com.grappim.hateitorrateit.analyticsapi.SettingsAnalytics
import com.grappim.hateitorrateit.analyticsimpl.DetailsAnalyticsImpl
import com.grappim.hateitorrateit.analyticsimpl.HomeAnalyticsImpl
import com.grappim.hateitorrateit.analyticsimpl.ProductManagerAnalyticsImpl
import com.grappim.hateitorrateit.analyticsimpl.SettingsAnalyticsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface ScreenAnalyticsModule {

    @Binds
    fun bindDetailsScreenAnalytics(detailsAnalyticsImpl: DetailsAnalyticsImpl): DetailsAnalytics

    @Binds
    fun bindSettingsScreenAnalytics(settingsAnalyticsImpl: SettingsAnalyticsImpl): SettingsAnalytics

    @Binds
    fun bindHomeScreeAnalytics(homeAnalyticsImpl: HomeAnalyticsImpl): HomeAnalytics

    @Binds
    fun bindProductManagerAnalytics(
        productManagerAnalyticsImpl: ProductManagerAnalyticsImpl
    ): ProductManagerAnalytics
}
