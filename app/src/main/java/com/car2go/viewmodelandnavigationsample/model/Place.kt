package com.car2go.viewmodelandnavigationsample.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(
    val name: String,
    val latitude: Double,
    val longitude: Double
): Parcelable