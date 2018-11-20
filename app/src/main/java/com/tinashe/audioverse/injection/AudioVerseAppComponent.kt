package com.tinashe.audioverse.injection

import com.tinashe.audioverse.AudioVerseApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AudioVerseAppModule::class, OfflineModule::class,
    AndroidInjectionModule::class, AndroidSupportInjectionModule::class, InjectionBinder::class])
interface AudioVerseAppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: AudioVerseApp): Builder

        fun build(): AudioVerseAppComponent
    }

    fun inject(app: AudioVerseApp)
}