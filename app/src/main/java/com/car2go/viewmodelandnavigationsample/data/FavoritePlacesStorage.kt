package com.car2go.viewmodelandnavigationsample.data

import android.content.SharedPreferences
import com.car2go.viewmodelandnavigationsample.di.qualifiers.StoragePreferences
import com.car2go.viewmodelandnavigationsample.model.Place
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.rxjava3.core.Observable
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

    fun load(): List<Place> {
        return sharedPreferences.getString(KEY, null)?.let {
            adapter.fromJson(it)
        } ?: emptyList()
    }

    fun observePlaces(): Observable<List<Place>> {
        return Observable.create { emitter ->

            emitter.onNext(load())

            val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
                if (key != KEY) return@OnSharedPreferenceChangeListener

                emitter.onNext(load())
            }

            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

            emitter.setCancellable {
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