package com.car2go.viewmodelandnavigationsample.viewmodel.settings

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SettingsViewState(
    val sortDescending: Boolean
): Parcelable