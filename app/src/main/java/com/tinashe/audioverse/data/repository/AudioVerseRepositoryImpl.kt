package com.tinashe.audioverse.data.repository

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.tinashe.audioverse.data.api.AudioVerseApi
import com.tinashe.audioverse.data.database.AudioVerseDb
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.model.RecordingType
import com.tinashe.audioverse.data.model.SearchResult
import com.tinashe.audioverse.data.repository.helper.PresentersDataFactory
import com.tinashe.audioverse.utils.Helper
import com.tinashe.audioverse.utils.RxSchedulers
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class AudioVerseRepositoryImpl constructor(private val audioVerseApi: AudioVerseApi,
                                           private val audioVerseDb: AudioVerseDb,
                                           private val rxSchedulers: RxSchedulers) : AudioVerseRepository {

    companion object {
        private const val PAGE_SIZE = 50

        private val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(PAGE_SIZE * 2)
                .setPageSize(PAGE_SIZE)
                .build()
    }

    override fun getPresenters(): Flowable<PagedList<Presenter>> {
        val sourceFactory = PresentersDataFactory(audioVerseApi, audioVerseDb)

        return RxPagedListBuilder(sourceFactory, pagedListConfig)
                .setFetchScheduler(rxSchedulers.network)
                .buildFlowable(BackpressureStrategy.LATEST)
    }

    override fun getRecordings(presenterId: String): Flowable<List<Recording>> {
        val api = audioVerseApi.getPresenterRecordings(presenterId)
                .subscribeOn(rxSchedulers.network)
                .flatMap {
                    if (it.isSuccessful) {
                        it.body()?.let {
                            val recordings = it.getRecordings()

                            Completable.fromAction {
                                audioVerseDb.recordingsDao().insertAll(recordings)
                            }.subscribeOn(rxSchedulers.database)
                                    .subscribe()

                            Observable.just(recordings)
                        }
                    } else {
                        Observable.error(RuntimeException(""))
                    }
                }
        val db = audioVerseDb.recordingsDao().listAll()
                .subscribeOn(rxSchedulers.database)
                .flatMap { Flowable.just(it.filter { it.presenters.isNotEmpty() && it.presenters.first().id == presenterId }) }


        return Flowable.merge(db, api.toFlowable(BackpressureStrategy.LATEST))
    }

    override fun getRecordings(type: RecordingType): Observable<List<Recording>> {
        val api = when (type) {
            RecordingType.NEW -> audioVerseApi.getNewRecordings()
            RecordingType.TRENDING -> audioVerseApi.getTrendingRecordings()
            RecordingType.FEATURED -> audioVerseApi.getFeaturedRecordings()
            else -> {
                Observable.error(Exception("Invalid category"))
            }
        }

        val tag = Helper.getTag(type)

        val apiResponse = api.subscribeOn(rxSchedulers.network)
                .flatMap {
                    if (it.isSuccessful) {
                        it.body()?.let {
                            val recordings = it.getRecordings()

                            recordings.map { it.tag = tag }

                            Completable.fromAction {
                                audioVerseDb.recordingsDao().insertAll(recordings)
                            }.subscribeOn(rxSchedulers.database)
                                    .subscribe()

                            Observable.just(recordings)
                        }
                    } else {
                        Observable.error(RuntimeException(""))
                    }
                }.cache()

        val db = audioVerseDb.recordingsDao().listByTag(tag)
                .subscribeOn(rxSchedulers.database)
                .toObservable()

        return Observable.merge(db, apiResponse)
    }

    override fun searchFor(query: String): Flowable<SearchResult> {

        val searchTerm = "%$query%"

        return Flowable.zip(audioVerseDb.recordingsDao().search(searchTerm), audioVerseDb.presentersDao().search(searchTerm),
                BiFunction { recordings, presenters -> SearchResult(recordings, presenters) })
    }
}