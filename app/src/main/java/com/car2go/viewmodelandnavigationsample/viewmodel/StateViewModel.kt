package com.car2go.viewmodelandnavigationsample.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

abstract class StateViewModel<State>: ViewModel() {

    val state: LiveData<State>
        get() = mutableState

    protected abstract val mutableState: MutableLiveData<State>
    abstract fun stateObservable(lifecycleOwner: LifecycleOwner): Observable<State>
    open fun doOnStart() {}

    private var disposable: Disposable? = null

    fun start(lifecycleOwner: LifecycleOwner) {
        disposable?.dispose()

        disposable = stateObservable(lifecycleOwner).subscribe {
            mutableState.postValue(it)
        }
    }

    override fun onCleared() {
        super.onCleared()

        disposable?.dispose()
        disposable = null
    }



}