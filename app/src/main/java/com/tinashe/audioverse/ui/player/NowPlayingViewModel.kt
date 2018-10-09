package com.tinashe.audioverse.ui.player

import androidx.lifecycle.MutableLiveData
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.repository.AudioVerseRepository
import com.tinashe.audioverse.ui.base.RxViewModel
import com.tinashe.audioverse.utils.RxSchedulers
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class NowPlayingViewModel @Inject constructor(private val repository: AudioVerseRepository,
                                              private val rxSchedulers: RxSchedulers) : RxViewModel() {

    var series = MutableLiveData<List<Recording>>()

    override fun subscribe() {

    }

    fun listSeries(recording: Recording) {
        var recordingsObservable: Observable<List<Recording>>? = null
        if (recording.series.isNotEmpty() && recording.seriesId?.isNotEmpty() == true) {
            recordingsObservable = repository.getSeries(recording.seriesId!!)
        } else if (recording.presenters.isNotEmpty() && recording.presenters.first().id.isNotEmpty()) {
            recordingsObservable = repository.getRecordings(recording.presenters.first().id).toObservable()
        }

        if (recordingsObservable == null) {
            series.postValue(emptyList())
            return
        }

        val disposable = recordingsObservable
                .observeOn(rxSchedulers.main)
                .subscribe({
                    series.postValue(it)
                }, {
                    Timber.e(it)
                    series.postValue(emptyList())
                })

        disposables.add(disposable)
    }
}