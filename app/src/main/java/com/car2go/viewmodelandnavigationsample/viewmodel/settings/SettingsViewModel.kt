package com.car2go.viewmodelandnavigationsample.viewmodel.settings

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.car2go.viewmodelandnavigationsample.data.SettingsStorage
import com.car2go.viewmodelandnavigationsample.viewmodel.StateViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsViewModel @ViewModelInject constructor(
    private val settingsStorage: SettingsStorage,
    @Assisted savedStateHandle: SavedStateHandle
): StateViewModel<SettingsViewState>() {

    override val mutableState: MutableLiveData<SettingsViewState> = savedStateHandle.getLiveData("SettingsViewModel.state")

    override fun stateFlow(): Flow<SettingsViewState> {
        return sortByDescendingFlow().map {
            SettingsViewState(sortDescending = it)
        }
    }

    private fun sortByDescendingFlow(): Flow<Boolean> {
        return settingsStorage.sortByNameDescendingFlow()
    }

    fun setSortDescending(sortDescending: Boolean) {
        settingsStorage.setSortByNameDescending(sortDescending)
    }

}