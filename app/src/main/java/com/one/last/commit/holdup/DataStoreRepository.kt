package com.one.last.commit.holdup

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataStoreRepository {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_usage_data")

    private val selectedAppsKey = stringSetPreferencesKey("selected_apps")

    private val countKey = { pkg: String -> intPreferencesKey("count_$pkg") }

    fun getSelectedApps(context: Context): Flow<Set<String>> {
        return context.dataStore.data.map { prefs ->
            prefs[selectedAppsKey] ?: emptySet()
        }
    }

    suspend fun setSelectedApps(context: Context, selectedApps: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[selectedAppsKey] = selectedApps
        }
    }

    suspend fun incrementUsage(context: Context, pkg: String) {
        context.dataStore.edit { preferences ->
            val count = preferences[countKey(pkg)] ?: 0
            preferences[countKey(pkg)] = count + 1
        }
    }

    fun getAppUsage(context: Context, pkg: String): Flow<Int> {
        return context.dataStore.data.map { prefs ->
            prefs[countKey(pkg)] ?: 0
        }
    }
}