package com.tinashe.audioverse.ui.home.tab

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.model.RecordingType
import com.tinashe.audioverse.data.model.Section
import com.tinashe.audioverse.data.repository.AudioVerseRepository
import com.tinashe.audioverse.ui.base.RxViewModel
import com.tinashe.audioverse.ui.base.SingleLiveEvent
import com.tinashe.audioverse.utils.RxSchedulers
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class TabViewModel @Inject constructor(private val repository: AudioVerseRepository,
                                       private val rxSchedulers: RxSchedulers) : RxViewModel() {

    private val sectionHolder = SingleLiveEvent<Section>()

    var presenters: MutableLiveData<PagedList<Presenter>> = MutableLiveData()
    var recordings = MutableLiveData<List<Recording>>()

    override fun subscribe() {
    }

    fun initTab(section: Section) {
        sectionHolder.value = section

        when (section) {

            Section.MY_LIST_FAVORITES -> {
            }
            Section.MY_LIST_PLAY_LISTS -> {
            }
            Section.MY_LIST_HISTORY -> {
            }
            Section.RECORDINGS_NEW -> {
                observeRecordings(repository.getRecordings(RecordingType.NEW))
            }
            Section.RECORDINGS_TRENDING -> {
                observeRecordings(repository.getRecordings(RecordingType.TRENDING))
            }
            Section.RECORDINGS_FEATURED -> {
                observeRecordings(repository.getRecordings(RecordingType.FEATURED))
            }
            Section.PRESENTERS -> {
                val disposable = repository.getPresenters()
                        .subscribe({ presenters.value = it }, { Timber.e(it, it.message) })

                disposables.add(disposable)
            }
        }
    }

    private fun observeRecordings(observable: Observable<List<Recording>>) {
        val disposable = observable
                .observeOn(rxSchedulers.main)
                .subscribe({
                    recordings.value = it
                }, { Timber.e(it, it.message) })

        disposables.add(disposable)
    }
}