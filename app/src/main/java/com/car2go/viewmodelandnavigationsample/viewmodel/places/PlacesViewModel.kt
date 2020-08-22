package com.car2go.viewmodelandnavigationsample.viewmodel.places

import android.os.Parcelable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.car2go.viewmodelandnavigationsample.data.FavoritePlacesStorage
import com.car2go.viewmodelandnavigationsample.model.Place
import com.car2go.viewmodelandnavigationsample.util.asObservable
import com.car2go.viewmodelandnavigationsample.viewmodel.StateViewModel
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observable.merge
import io.reactivex.rxjava3.kotlin.Observables.combineLatest
import kotlinx.android.parcel.Parcelize

class PlacesViewModel @ViewModelInject constructor(
    private val favoritePlacesStorage: FavoritePlacesStorage,
    @Assisted savedStateHandle: SavedStateHandle
): StateViewModel<PlacesViewState>() {

    override val mutableState = savedStateHandle.getLiveData<PlacesViewState>("PlacesViewModel.state")
    private val newPlaceData = savedStateHandle.getLiveData<NewPlaceData>("PlacesViewModel.newPlaceData")

    private val shouldEditRelay = PublishRelay.create<Unit>()
    private val shouldCancelRelay = PublishRelay.create<Unit>()
    private val shouldAddPlaceRelay = PublishRelay.create<Unit>()

    override fun doOnStart() {
        super.doOnStart()

        if (mutableState.value == null) {
            mutableState.postValue(initialState())
        }

        if (newPlaceData.value == null) {
            newPlaceData.postValue(NewPlaceData())
        }
    }

    fun startEditing() {
        shouldEditRelay.accept(Unit)
    }

    fun cancelEditing() {
        shouldCancelRelay.accept(Unit)
    }

    fun addNewPlace() {
        shouldAddPlaceRelay.accept(Unit)
    }

    fun setLatitude(latitude: Double?) {
        newPlaceData.value?.let {
            newPlaceData.postValue(it.copy(latitude = latitude))
        }
    }

    fun setLongitude(longitude: Double?) {
        newPlaceData.value?.let {
            newPlaceData.postValue(it.copy(longitude = longitude))
        }
    }

    fun setName(name: String?) {
        newPlaceData.value?.let {
            newPlaceData.postValue(it.copy(name = name))
        }
    }

    override fun stateObservable(lifecycleOwner: LifecycleOwner): Observable<PlacesViewState> {
        return combineLatest(
            isEditingObservable(lifecycleOwner),
            favoritePlacesStorage.observePlaces(),
            newPlaceData.asObservable(lifecycleOwner)
        ) { isEditing, places, newPlaceData ->
            PlacesViewState(
                editing = isEditing,
                places = places,
                latitude = newPlaceData.latitude,
                longitude = newPlaceData.longitude,
                name = newPlaceData.name,
                canSave = newPlaceData.isComplete
            )
        }
    }

    private fun isEditingObservable(lifecycleOwner: LifecycleOwner): Observable<Boolean> {
        return merge(
            shouldCancelObservable(),
            shouldEditObservable(),
            addPlaceObservable(lifecycleOwner)
        )
            .startWith(Observable.fromCallable {
                mutableState.value?.editing ?: false
            })
            .doOnNext {
                if (!it) {
                    newPlaceData.postValue(NewPlaceData())
                }
            }
    }

    private fun addPlaceObservable(lifecycleOwner: LifecycleOwner): Observable<Boolean> {
        return shouldAddPlaceRelay
            .switchMap {
                combineLatest(
                    mutableState.asObservable(lifecycleOwner).map { it.places },
                    newPlaceData.asObservable(lifecycleOwner).filter { it.isComplete }
                ) { places, newPlaceData ->
                    places + Place(
                        name = newPlaceData.name!!,
                        latitude = newPlaceData.latitude!!,
                        longitude = newPlaceData.longitude!!
                    )
                }
                    .take(1)
            }
            .doOnNext {
                favoritePlacesStorage.save(it)
            }
            .map { false }
    }

    private fun shouldEditObservable(): Observable<Boolean> {
        return shouldEditRelay
            .map {
                true
            }
    }

    private fun shouldCancelObservable(): Observable<Boolean> {
        return shouldCancelRelay
            .map {
                false
            }
    }

    private fun initialState(): PlacesViewState {
        return PlacesViewState(
            editing = false,
            places = favoritePlacesStorage.load() ?: emptyList(),
            latitude = null,
            longitude = null,
            name = null,
            canSave = false
        )
    }

    @Parcelize
    private data class NewPlaceData(
        val latitude: Double? = null,
        val longitude: Double? = null,
        val name: String? = null
    ): Parcelable {

        val isComplete: Boolean
            get() {
                return !name.isNullOrEmpty() && latitude != null && longitude != null
            }

    }

}