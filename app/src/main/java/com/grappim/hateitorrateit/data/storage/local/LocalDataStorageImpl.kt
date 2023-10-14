package com.grappim.hateitorrateit.data.storage.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.grappim.hateitorrateit.core.HateRateType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocalDataStorage {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hateitratestore")

    private val TYPE_KEY = stringPreferencesKey("type_key")
    override val typeFlow: Flow<HateRateType> = context.dataStore.data
        .map { preferences ->
            enumValueOf(preferences[TYPE_KEY] ?: HateRateType.HATE.name)
        }

    override suspend fun changeTypeTo(type: HateRateType) {
        context.dataStore.edit { settings ->
            settings[TYPE_KEY] = type.name
        }
    }
}
