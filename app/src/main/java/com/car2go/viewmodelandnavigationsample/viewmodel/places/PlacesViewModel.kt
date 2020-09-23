package com.car2go.viewmodelandnavigationsample.viewmodel.places

import android.os.Parcelable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.car2go.viewmodelandnavigationsample.data.FavoritePlacesStorage
import com.car2go.viewmodelandnavigationsample.data.SettingsStorage
import com.car2go.viewmodelandnavigationsample.model.Place
import com.car2go.viewmodelandnavigationsample.viewmodel.StateViewModel
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class PlacesViewModel @ViewModelInject constructor(
    private val favoritePlacesStorage: FavoritePlacesStorage,
    private val settingsStorage: SettingsStorage,
    @Assisted savedStateHandle: SavedStateHandle
): StateViewModel<PlacesViewState>() {

    override val mutableState = savedStateHandle.getLiveData<PlacesViewState>("PlacesViewModel.state")
    private val newPlaceData = savedStateHandle.getLiveData<NewPlaceData>("PlacesViewModel.newPlaceData")

    private val shouldEditChannel = BroadcastChannel<Unit>(1)
    private val shouldCancelChannel = BroadcastChannel<Unit>(1)
    private val shouldAddPlaceChannel = BroadcastChannel<Unit>(1)

    override fun doOnStart() {
        super.doOnStart()

        if (mutableState.value == null) {
            mutableState.postValue(PlacesViewState())
        }

        if (newPlaceData.value == null) {
            newPlaceData.postValue(NewPlaceData())
        }
    }

    fun startEditing() {
        viewModelScope.launch {
            shouldEditChannel.send(Unit)
        }
    }

    fun cancelEditing() {
        viewModelScope.launch {
            shouldCancelChannel.send(Unit)
        }
    }

    fun addNewPlace() {
        viewModelScope.launch {
            shouldAddPlaceChannel.send(Unit)
        }
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

    override fun stateFlow(): Flow<PlacesViewState> {
        return combine(
            isEditingFlow(),
            favoritePlacesStorage.placesFlow(),
            settingsStorage.sortByNameDescendingFlow(),
            newPlaceData.asFlow()
        ) { isEditing, places, sortByNameDescending, newPlaceData ->
            PlacesViewState(
                editing = isEditing,
                places = places.sorted(sortByNameDescending),
                latitude = newPlaceData.latitude,
                longitude = newPlaceData.longitude,
                name = newPlaceData.name,
                canSave = newPlaceData.isComplete
            )
        }
    }

    private fun isEditingFlow(): Flow<Boolean> {
        return merge(
            shouldCancelFlow(),
            shouldEditFlow(),
            addPlaceFlow()
        )
            .onStart {
                emit(mutableState.value?.editing ?: false)
            }
            .onEach {
                if (!it) {
                    newPlaceData.postValue(NewPlaceData())
                }
            }
    }

    private fun addPlaceFlow(): Flow<Boolean> {
        return shouldAddPlaceChannel
            .asFlow()
            .flatMapLatest {
                combine(
                    mutableState.asFlow().map { it.places },
                    newPlaceData.asFlow().filter { it.isComplete }
                ) { places, newPlaceData ->
                    places + Place(
                        name = newPlaceData.name!!,
                        latitude = newPlaceData.latitude!!,
                        longitude = newPlaceData.longitude!!
                    )
                }
                    .take(1)
            }
            .onEach {
                favoritePlacesStorage.save(it)
            }
            .map { false }
    }

    private fun shouldEditFlow(): Flow<Boolean> {
        return shouldEditChannel
            .asFlow()
            .map {
                true
            }
    }

    private fun shouldCancelFlow(): Flow<Boolean> {
        return shouldCancelChannel
            .asFlow()
            .map {
                false
            }
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

    private companion object {
        fun List<Place>.sorted(byNameDescending: Boolean) = if (byNameDescending) {
            sortedByDescending { it.name }
        } else {
            sortedBy { it.name }
        }
    }

}