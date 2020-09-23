package com.car2go.viewmodelandnavigationsample.data

import android.content.SharedPreferences
import com.car2go.viewmodelandnavigationsample.di.qualifiers.StoragePreferences
import com.car2go.viewmodelandnavigationsample.model.Place
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FavoritePlacesStorage @Inject constructor(
    private val moshi: Moshi,
    @StoragePreferences private val sharedPreferences: SharedPreferences
) {

    private val adapter: JsonAdapter<List<Place>> by lazy {
        moshi.adapter<List<Place>>(Types.newParameterizedType(
            List::class.java,
            Place::class.java
        ))
    }

    private fun load(): List<Place> {
        return sharedPreferences.getString(KEY, null)?.let {
            adapter.fromJson(it)
        } ?: emptyList()
    }

    @ExperimentalCoroutinesApi
    fun placesFlow(): Flow<List<Place>> {
        return callbackFlow {

            offer(load())

            val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
                if (key != KEY) return@OnSharedPreferenceChangeListener

                offer(load())
            }

            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

            awaitClose {
                sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }

    fun save(places: List<Place>) {
        val serialized = adapter.toJson(places)

        sharedPreferences.edit().putString(KEY, serialized).apply()
    }

    private companion object {
        private const val KEY = "places"
    }

}