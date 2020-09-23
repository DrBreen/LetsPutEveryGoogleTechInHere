package com.car2go.viewmodelandnavigationsample.viewmodel.places

import android.os.Parcelable
import com.car2go.viewmodelandnavigationsample.model.Place
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlacesViewState(
    val editing: Boolean = false,
    val places: List<Place> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val name: String? = null,
    val canSave: Boolean = false
): Parcelable