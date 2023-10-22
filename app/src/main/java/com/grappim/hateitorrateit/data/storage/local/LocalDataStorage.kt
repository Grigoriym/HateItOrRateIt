package com.grappim.hateitorrateit.data.storage.local

import com.grappim.domain.HateRateType
import kotlinx.coroutines.flow.Flow

interface LocalDataStorage {
    val typeFlow: Flow<HateRateType>
    suspend fun changeTypeTo(type: HateRateType)
}