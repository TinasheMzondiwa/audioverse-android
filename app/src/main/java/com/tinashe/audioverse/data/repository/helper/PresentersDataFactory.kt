package com.tinashe.audioverse.data.repository.helper

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.tinashe.audioverse.data.api.AudioVerseApi
import com.tinashe.audioverse.data.database.AudioVerseDb
import com.tinashe.audioverse.data.model.Presenter
import timber.log.Timber
import java.io.IOException

class PresentersDataFactory constructor(private val audioVerseApi: AudioVerseApi,
                                        private val audioVerseDb: AudioVerseDb,
                                        private val networkHelper: NetworkHelper) : DataSource.Factory<Int, Presenter>() {

    private val sourceLiveData = MutableLiveData<PresentersSource>()

    override fun create(): DataSource<Int, Presenter> {

        val source = PresentersSource(audioVerseApi, audioVerseDb, networkHelper)
        sourceLiveData.postValue(source)
        return source
    }

    inner class PresentersSource constructor(private val api: AudioVerseApi,
                                       private val database: AudioVerseDb,
                                       private val networkHelper: NetworkHelper) : ItemKeyedDataSource<Int, Presenter>() {

        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Presenter>) {

            val cache = database.presentersDao().listAllDirect()

            if (cache.isEmpty() && networkHelper.hasConnection()) {

                try {
                    val response = api.listPresenters().execute()
                    if (response.isSuccessful) {
                        response.body()?.let {
                            database.presentersDao().insertAll(it.presenters)
                            callback.onResult(database.presentersDao().listAllDirect())
                        }
                    }
                } catch (ex: IOException) {
                    Timber.d(ex)
                }

            } else {
                callback.onResult(cache)
            }
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Presenter>) {
            val db = database.presentersDao().listAllDirect()

            callback.onResult(db)
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Presenter>) {

            if (networkHelper.hasConnection()) {
                val response = api.listPresenters().execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        database.presentersDao().insertAll(it.presenters)
                    }
                }
            }

        }

        override fun getKey(item: Presenter): Int = item.id.toInt()

    }

}