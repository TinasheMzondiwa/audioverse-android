package com.tinashe.audioverse.ui.presenter

import androidx.lifecycle.MutableLiveData
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.repository.AudioVerseRepository
import com.tinashe.audioverse.ui.base.RxViewModel
import com.tinashe.audioverse.utils.RxSchedulers
import timber.log.Timber
import javax.inject.Inject

class PresenterViewModel @Inject constructor(private val repository: AudioVerseRepository,
                                             private val rxSchedulers: RxSchedulers) : RxViewModel() {

    var presentations = MutableLiveData<List<Recording>>()

    override fun subscribe() {

    }

    fun fetchPresentations(presenterId: String) {
        val disposable = repository.getRecordings(presenterId)
                .observeOn(rxSchedulers.main)
                .subscribe({
                    presentations.value = it.sortedByDescending {
                        it.recordingDate
                    }
                }, {
                    Timber.e(it, it.message)
                    presentations.value = null
                })

        disposables.add(disposable)
    }
}