package com.tinashe.audioverse.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class RxViewModel : ViewModel() {

    val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        unSubscribe()
    }

    abstract fun subscribe()

    open fun unSubscribe() {
        disposables.clear()
    }
}