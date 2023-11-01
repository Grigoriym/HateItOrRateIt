package com.grappim.hateitorrateit.data.storage.local

import com.grappim.domain.HateRateType
import kotlinx.coroutines.flow.Flow

interface LocalDataStorage {
    val typeFlow: Flow<HateRateType>
    val crashesCollectionEnabled: Flow<Boolean>
    suspend fun changeTypeTo(type: HateRateType)
    suspend fun setCrashesCollectionEnabled(isEnabled: Boolean)
}