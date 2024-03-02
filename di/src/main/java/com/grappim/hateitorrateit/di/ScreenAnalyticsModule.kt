package com.grappim.hateitorrateit.di

import com.grappim.hateitorrateit.analyticsapi.DetailsScreenAnalytics
import com.grappim.hateitorrateit.analyticsapi.HomeScreenAnalytics
import com.grappim.hateitorrateit.analyticsapi.ProductManagerAnalytics
import com.grappim.hateitorrateit.analyticsapi.SettingsScreenAnalytics
import com.grappim.hateitorrateit.analyticsimpl.DetailsScreenAnalyticsImpl
import com.grappim.hateitorrateit.analyticsimpl.HomeScreenAnalyticsImpl
import com.grappim.hateitorrateit.analyticsimpl.ProductManagerAnalyticsImpl
import com.grappim.hateitorrateit.analyticsimpl.SettingsScreenAnalyticsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface ScreenAnalyticsModule {

    @Binds
    fun bindDetailsScreenAnalytics(
        detailsScreenAnalyticsImpl: DetailsScreenAnalyticsImpl
    ): DetailsScreenAnalytics

    @Binds
    fun bindSettingsScreenAnalytics(
        settingsScreenAnalyticsImpl: SettingsScreenAnalyticsImpl
    ): SettingsScreenAnalytics

    @Binds
    fun bindHomeScreeAnalytics(
        homeScreenAnalyticsImpl: HomeScreenAnalyticsImpl
    ): HomeScreenAnalytics

    @Binds
    fun bindProductManagerAnalytics(
        productManagerAnalyticsImpl: ProductManagerAnalyticsImpl
    ): ProductManagerAnalytics
}
