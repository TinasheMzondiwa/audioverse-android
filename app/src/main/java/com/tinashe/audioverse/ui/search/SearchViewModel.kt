package com.tinashe.audioverse.ui.search

import androidx.lifecycle.MutableLiveData
import com.tinashe.audioverse.data.model.SearchResult
import com.tinashe.audioverse.data.repository.AudioVerseRepository
import com.tinashe.audioverse.ui.base.RxViewModel
import com.tinashe.audioverse.utils.RxSchedulers
import timber.log.Timber
import javax.inject.Inject

class SearchViewModel @Inject constructor(private val rxSchedulers: RxSchedulers,
                                          private val repository: AudioVerseRepository) : RxViewModel() {

    var searchResults = MutableLiveData<SearchResult>()

    override fun subscribe() {

    }

    fun search(query: String?) {
        if (query == null || query.isEmpty()) {
            searchResults.value = SearchResult(emptyList(), emptyList())
            return
        }
        val disposable = repository.searchFor(query)
                .subscribeOn(rxSchedulers.database)
                .observeOn(rxSchedulers.main)
                .subscribe({
                    searchResults.value = it

                }, {
                    Timber.e(it, it.message)
                })

        disposables.add(disposable)
    }
}