package com.car2go.viewmodelandnavigationsample.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.rxjava3.core.Observable

fun <T> LiveData<T>.asObservable(lifecycleOwner: LifecycleOwner): Observable<T> {
    return Observable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, this))
}