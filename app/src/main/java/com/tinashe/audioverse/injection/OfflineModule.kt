package com.tinashe.audioverse.injection

import android.content.Context
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util
import com.tinashe.audioverse.media.DownloadTracker
import com.tinashe.audioverse.media.MusicService
import dagger.Module
import dagger.Provides
import java.io.File
import javax.inject.Singleton


@Module
internal class OfflineModule {

    @Provides
    @Singleton
    fun provideDownloadManager(context: Context): DownloadManager {
        val downloaderConstructorHelper = DownloaderConstructorHelper(provideDownloadCache(context),
                buildHttpDataSourceFactory(context))

        val downloadManager = DownloadManager(downloaderConstructorHelper,
                MAX_SIMULTANEOUS_DOWNLOADS,
                DownloadManager.DEFAULT_MIN_RETRY_COUNT,
                File(getDownloadDirectory(context), DOWNLOAD_ACTION_FILE))

        val downloadTracker = DownloadTracker(context,
                getDataSourceFactory(context),
                File(getDownloadDirectory(context), DOWNLOAD_TRACKER_ACTION_FILE))
        downloadManager.addListener(downloadTracker)

        return downloadManager
    }

    @Provides
    @Singleton
    fun provideDefaultDataSourceFactory(context: Context): DefaultDataSourceFactory {
        return DefaultDataSourceFactory(context,
                Util.getUserAgent(context, MusicService.AVMP_USER_AGENT))
    }

    @Provides
    @Singleton
    fun buildReadOnlyCacheDataSource(upstreamFactory: DefaultDataSourceFactory, cache: Cache): CacheDataSourceFactory {
        return CacheDataSourceFactory(
                cache,
                upstreamFactory,
                FileDataSourceFactory(),
                /* eventListener= */ null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null)/* cacheWriteDataSinkFactory= */
    }

    @Provides
    @Singleton
    fun provideDownloadCache(context: Context): Cache {
        val downloadContentDirectory = File(getDownloadDirectory(context), DOWNLOAD_CONTENT_DIRECTORY)
        return cacheInstance(downloadContentDirectory)
    }

    private fun getDataSourceFactory(context: Context): DataSource.Factory {
        return buildReadOnlyCacheDataSource(provideDefaultDataSourceFactory(context), provideDownloadCache(context))
    }

    private fun getDownloadDirectory(context: Context): File {
        var downloadDirectory = context.getExternalFilesDir(null)
        if (downloadDirectory == null) {
            downloadDirectory = context.filesDir
        }
        return downloadDirectory!!
    }

    private fun buildHttpDataSourceFactory(context: Context): HttpDataSource.Factory {
        return DefaultHttpDataSourceFactory(Util.getUserAgent(context, MusicService.AVMP_USER_AGENT))
    }

    companion object {
        private const val DOWNLOAD_ACTION_FILE = "actions"
        private const val DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions"
        private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
        private const val MAX_SIMULTANEOUS_DOWNLOADS = 2

        var instance: SimpleCache? = null

        @Synchronized
        fun cacheInstance(downloadContentDirectory: File): SimpleCache {
            return instance ?: SimpleCache(downloadContentDirectory, NoOpCacheEvictor()).also {
                instance = it
            }
        }
    }
}