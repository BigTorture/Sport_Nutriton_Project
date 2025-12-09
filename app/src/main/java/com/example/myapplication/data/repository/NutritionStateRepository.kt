package com.example.myapplication.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.data.model.AppState
import com.example.myapplication.data.model.BodyMetric
import com.example.myapplication.data.model.DiaryEntry
import com.example.myapplication.data.model.NutritionReminder
import com.example.myapplication.data.model.UserProfile
import com.example.myapplication.data.model.WaterLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.UUID

private val Context.appStateDataStore by preferencesDataStore(name = "nutrition_state")

class NutritionStateRepository(context: Context) {

    private val dataStore = context.appStateDataStore
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    private val stateKey: Preferences.Key<String> = stringPreferencesKey("app_state")
    private val mutex = Mutex()

    val appStateFlow: Flow<AppState> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            prefs[stateKey]?.let { stored ->
                runCatching { json.decodeFromString<AppState>(stored) }
                    .getOrElse { AppState() }
            } ?: AppState()
        }

    suspend fun upsertProfile(profile: UserProfile, setActive: Boolean = false): UserProfile {
        updateState { current ->
            val profiles = current.profiles.toMutableList()
            val index = profiles.indexOfFirst { it.id == profile.id }
            if (index >= 0) {
                profiles[index] = profile
            } else {
                profiles.add(profile)
            }
            val activeId = when {
                setActive -> profile.id
                current.activeProfileId == null -> profile.id
                current.profiles.none { it.id == current.activeProfileId } -> profile.id
                else -> current.activeProfileId
            }

            current.copy(
                profiles = profiles,
                activeProfileId = activeId
            )
        }
        return profile
    }

    suspend fun setActiveProfile(profileId: String) {
        updateState { it.copy(activeProfileId = profileId) }
    }

    suspend fun clearActiveProfile() {
        updateState { it.copy(activeProfileId = null) }
    }

    suspend fun addBodyMetric(metric: BodyMetric) {
        updateState { current ->
            current.copy(bodyMetrics = current.bodyMetrics + metric)
        }
    }

    suspend fun addDiaryEntry(entry: DiaryEntry) {
        updateState { current ->
            current.copy(diaryEntries = current.diaryEntries + entry)
        }
    }

    suspend fun deleteDiaryEntry(entryId: String) {
        updateState { current ->
            current.copy(diaryEntries = current.diaryEntries.filterNot { it.id == entryId })
        }
    }

    suspend fun addReminder(reminder: NutritionReminder) {
        val actualReminder = if (reminder.id.isEmpty()) reminder.copy(id = UUID.randomUUID().toString()) else reminder
        updateState { current ->
            current.copy(reminders = current.reminders.filterNot { it.id == actualReminder.id } + actualReminder)
        }
    }

    suspend fun toggleReminder(reminderId: String, enabled: Boolean) {
        updateState { current ->
            current.copy(
                reminders = current.reminders.map {
                    if (it.id == reminderId) it.copy(enabled = enabled) else it
                }
            )
        }
    }

    suspend fun deleteReminder(reminderId: String) {
        updateState { current ->
            current.copy(reminders = current.reminders.filterNot { it.id == reminderId })
        }
    }

    suspend fun addWaterLog(log: WaterLog) {
        updateState { current ->
            current.copy(waterLogs = current.waterLogs + log)
        }
    }

    suspend fun deleteWaterLog(logId: String) {
        updateState { current ->
            current.copy(waterLogs = current.waterLogs.filterNot { it.id == logId })
        }
    }

    private suspend fun updateState(transform: (AppState) -> AppState) {
        mutex.withLock {
            dataStore.edit { prefs ->
                val current = prefs[stateKey]?.let { stored ->
                    runCatching { json.decodeFromString<AppState>(stored) }.getOrElse { AppState() }
                } ?: AppState()
                val updated = transform(current)
                prefs[stateKey] = json.encodeToString(updated)
            }
        }
    }
}

