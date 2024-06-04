package com.grappim.hateitorrateit.data.analyticsimpl

import com.grappim.hateitorrateit.data.analyticsapi.DetailsAnalytics
import com.grappim.hateitorrateit.data.analyticsapi.HomeAnalytics
import com.grappim.hateitorrateit.data.analyticsapi.ProductManagerAnalytics
import com.grappim.hateitorrateit.data.analyticsapi.SettingsAnalytics
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
