package com.grappim.hateitorrateit.data.localdatastorageimpl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.domain.HateRateType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataStorageImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : LocalDataStorage {

    private val typeKey = stringPreferencesKey("type_key")
    override val typeFlow: Flow<HateRateType> = dataStore.data
        .map { preferences ->
            enumValueOf(preferences[typeKey] ?: HateRateType.HATE.name)
        }

    private val crashesKey = booleanPreferencesKey("crashes_key")
    override val crashesCollectionEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[crashesKey] ?: true
        }

    override suspend fun setCrashesCollectionEnabled(isEnabled: Boolean) {
        Timber.d("setCrashesCollectionEnabled: $isEnabled")
        dataStore.edit { settings ->
            settings[crashesKey] = isEnabled
        }
    }

    override suspend fun changeTypeTo(type: HateRateType) {
        dataStore.edit { settings ->
            settings[typeKey] = type.name
        }
    }
}
