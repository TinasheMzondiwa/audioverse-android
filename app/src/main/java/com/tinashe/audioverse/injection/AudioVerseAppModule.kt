package com.tinashe.audioverse.injection

import android.content.ComponentName
import android.content.Context
import com.tinashe.audioverse.AudioVerseApp
import com.tinashe.audioverse.data.api.AudioVerseApi
import com.tinashe.audioverse.data.api.RestClient
import com.tinashe.audioverse.data.database.AudioVerseDb
import com.tinashe.audioverse.data.database.dao.RecordingsDao
import com.tinashe.audioverse.data.repository.AudioVerseRepository
import com.tinashe.audioverse.data.repository.AudioVerseRepositoryImpl
import com.tinashe.audioverse.data.repository.helper.NetworkHelper
import com.tinashe.audioverse.media.MediaSessionConnection
import com.tinashe.audioverse.media.MusicService
import com.tinashe.audioverse.utils.RxSchedulers
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class AudioVerseAppModule {

    @Provides
    @Singleton
    fun provideContext(app: AudioVerseApp): Context = app

    @Provides
    @Singleton
    fun provideRxSchedulers(): RxSchedulers = RxSchedulers()

    @Provides
    @Singleton
    fun provideApi(): AudioVerseApi = RestClient.createService(AudioVerseApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AudioVerseDb = AudioVerseDb.create(context)

    @Provides
    @Singleton
    fun provideRecordingsDao(database: AudioVerseDb): RecordingsDao = database.recordingsDao()

    @Provides
    @Singleton
    fun provideRepository(api: AudioVerseApi, db: AudioVerseDb, schedulers: RxSchedulers, context: Context):
            AudioVerseRepository = AudioVerseRepositoryImpl(api, db, schedulers, NetworkHelper(context))

    @Provides
    @Singleton
    fun provideMediaSessionConnection(context: Context): MediaSessionConnection {
        return MediaSessionConnection.getInstance(context,
                ComponentName(context, MusicService::class.java))
    }
}

