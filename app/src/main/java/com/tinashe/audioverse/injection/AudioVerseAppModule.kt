package com.tinashe.audioverse.injection

import android.content.Context
import com.tinashe.audioverse.AudioVerseApp
import com.tinashe.audioverse.data.api.AudioVerseApi
import com.tinashe.audioverse.data.api.RestClient
import com.tinashe.audioverse.data.database.AudioVerseDb
import com.tinashe.audioverse.data.repository.AudioVerseRepository
import com.tinashe.audioverse.data.repository.AudioVerseRepositoryImpl
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
    fun provideRepository(api: AudioVerseApi, db: AudioVerseDb, schedulers: RxSchedulers):
            AudioVerseRepository = AudioVerseRepositoryImpl(api, db, schedulers)
}

