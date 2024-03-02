package com.grappim.hateitorrateit.data.localdatastorageimpl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.domain.DarkThemeConfig
import com.grappim.hateitorrateit.domain.HateRateType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataStorageImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
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

    private val analyticsKey = booleanPreferencesKey("analytics_key")
    override val analyticsCollectionEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[analyticsKey] ?: true
        }

    private val darkThemeKey = stringPreferencesKey("dark_theme_key")
    override val darkThemeConfig: Flow<DarkThemeConfig> = dataStore.data
        .map { preferences ->
            DarkThemeConfig.fromValue(preferences[darkThemeKey]) ?: DarkThemeConfig.default()
        }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        Timber.d("setDarkThemeConfig: $darkThemeConfig")
        dataStore.edit { settings ->
            settings[darkThemeKey] = darkThemeConfig.value
        }
    }

    override suspend fun setCrashesCollectionEnabled(isEnabled: Boolean) {
        Timber.d("setCrashesCollectionEnabled: $isEnabled")
        dataStore.edit { settings ->
            settings[crashesKey] = isEnabled
        }
    }

    override suspend fun setAnalyticsCollectionEnabled(isEnabled: Boolean) {
        Timber.d("setAnalyticsCollectionEnabled: $isEnabled")
        dataStore.edit { settings ->
            settings[analyticsKey] = isEnabled
        }
    }

    override suspend fun changeTypeTo(type: HateRateType) {
        Timber.d("changeTypeTo: $type")
        dataStore.edit { settings ->
            settings[typeKey] = type.name
        }
    }
}
