package com.tinashe.audioverse.data.repository.helper

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.tinashe.audioverse.data.api.AudioVerseApi
import com.tinashe.audioverse.data.database.AudioVerseDb
import com.tinashe.audioverse.data.model.Presenter
import timber.log.Timber

class PresentersDataFactory constructor(private val audioVerseApi: AudioVerseApi,
                                        private val audioVerseDb: AudioVerseDb) : DataSource.Factory<Int, Presenter>() {

    val sourceLiveData = MutableLiveData<PresentersSource>()

    override fun create(): DataSource<Int, Presenter> {

        val source = PresentersSource(audioVerseApi, audioVerseDb)
        sourceLiveData.postValue(source)
        return source
    }

    class PresentersSource constructor(private val api: AudioVerseApi,
                                       private val database: AudioVerseDb) : ItemKeyedDataSource<Int, Presenter>() {

        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Presenter>) {

            val cache = database.presentersDao().listAllDirect()

            if (cache.isEmpty()) {

                val response = api.listPresenters().execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        database.presentersDao().insertAll(it.getPresenterss())
                        callback.onResult(database.presentersDao().listAllDirect())
                    }
                }
            } else {
                callback.onResult(cache)
            }
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Presenter>) {
            //
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Presenter>) {
            //
            Timber.d("lodBefore")

            val response = api.listPresenters().execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    database.presentersDao().insertAll(it.getPresenterss())
                }
            }
        }

        override fun getKey(item: Presenter): Int = item.id.toInt()

    }

}