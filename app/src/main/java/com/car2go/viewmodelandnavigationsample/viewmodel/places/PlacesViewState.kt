package com.car2go.viewmodelandnavigationsample.viewmodel.places

import android.os.Parcelable
import com.car2go.viewmodelandnavigationsample.model.Place
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlacesViewState(
    val editing: Boolean,
    val places: List<Place>,
    val latitude: Double?,
    val longitude: Double?,
    val name: String?,
    val canSave: Boolean
): Parcelable