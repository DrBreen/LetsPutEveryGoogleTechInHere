package com.car2go.viewmodelandnavigationsample.data

import android.content.SharedPreferences
import com.car2go.viewmodelandnavigationsample.di.qualifiers.SettingsPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SettingsStorage @Inject constructor(
    @SettingsPreferences private val sharedPreferences: SharedPreferences
) {

    private fun sortByNameDescendingFlowSync(): Boolean = sharedPreferences.getBoolean(KEY_SORT_BY_NAME_DESCENDING, false)

    @ExperimentalCoroutinesApi
    fun sortByNameDescendingFlow(): Flow<Boolean> {
        return callbackFlow {
            offer(sortByNameDescendingFlowSync())

            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == KEY_SORT_BY_NAME_DESCENDING) {
                    offer(sortByNameDescendingFlowSync())
                }
            }
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

            awaitClose {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }

    fun setSortByNameDescending(sortDescending: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_SORT_BY_NAME_DESCENDING, sortDescending).apply()
    }

    private companion object {
        private const val KEY_SORT_BY_NAME_DESCENDING = "sort_by_name_descending"
    }

}