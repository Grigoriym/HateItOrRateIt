package com.grappim.hateitorrateit.data.localdatastorageapi

import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import kotlinx.coroutines.flow.Flow

interface LocalDataStorage {
    val typeFlow: Flow<HateRateType>
    val crashesCollectionEnabled: Flow<Boolean>
    val analyticsCollectionEnabled: Flow<Boolean>
    val darkThemeConfig: Flow<DarkThemeConfig>
    suspend fun changeTypeTo(type: HateRateType)
    suspend fun setCrashesCollectionEnabled(isEnabled: Boolean)

    suspend fun setAnalyticsCollectionEnabled(isEnabled: Boolean)

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)
}
