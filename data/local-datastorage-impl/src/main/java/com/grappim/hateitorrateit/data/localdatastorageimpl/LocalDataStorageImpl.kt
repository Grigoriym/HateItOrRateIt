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

    private val TYPE_KEY = stringPreferencesKey("type_key")
    override val typeFlow: Flow<HateRateType> = dataStore.data
        .map { preferences ->
            enumValueOf(preferences[TYPE_KEY] ?: HateRateType.HATE.name)
        }

    private val CRASHES_KEY = booleanPreferencesKey("crashes_key")
    override val crashesCollectionEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[CRASHES_KEY] ?: true
        }

    override suspend fun setCrashesCollectionEnabled(isEnabled: Boolean) {
        Timber.d("setCrashesCollectionEnabled: $isEnabled")
        dataStore.edit { settings ->
            settings[CRASHES_KEY] = isEnabled
        }
    }

    override suspend fun changeTypeTo(type: HateRateType) {
        dataStore.edit { settings ->
            settings[TYPE_KEY] = type.name
        }
    }
}
