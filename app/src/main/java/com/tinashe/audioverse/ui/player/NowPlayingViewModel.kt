package com.tinashe.audioverse.ui.player

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.repository.AudioVerseRepository
import com.tinashe.audioverse.media.EMPTY_PLAYBACK_STATE
import com.tinashe.audioverse.media.MediaSessionConnection
import com.tinashe.audioverse.media.NOTHING_PLAYING
import com.tinashe.audioverse.media.extensions.id
import com.tinashe.audioverse.media.extensions.isPlayEnabled
import com.tinashe.audioverse.media.extensions.isPlaying
import com.tinashe.audioverse.ui.base.RxViewModel
import com.tinashe.audioverse.utils.RxSchedulers
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class NowPlayingViewModel @Inject constructor(private val repository: AudioVerseRepository,
                                              private val rxSchedulers: RxSchedulers,
                                              mediaSessionConnection: MediaSessionConnection) : RxViewModel() {

    var relatedMedia = MutableLiveData<List<Recording>>()

    var playbackState = MutableLiveData<PlaybackStateCompat>()
    var nowPlaying = MutableLiveData<Recording>()
    var thumbedUp = MutableLiveData<Boolean>()

    private var mediaId = "123"

    override fun subscribe() {

    }

    /**
     * When [MediaSessionConnection] is connected we want to immediately play this recording.
     *
     * We also want to save it as the current [nowPlaying] item
     */
    fun setMediaItem(recording: Recording) {
        nowPlaying.postValue(recording)

        mediaSessionConnection.isConnected.value?.let {
            if (it) {
                playMedia(recording)
            }
        }
    }

    /**
     * List recordings relatedMedia to the current one playing
     */
    fun listRelated(recording: Recording) {
        var recordingsObservable: Observable<List<Recording>>? = null
        if (recording.series.isNotEmpty() && recording.seriesId?.isNotEmpty() == true) {
            recordingsObservable = repository.getSeries(recording.seriesId!!)
        } else if (recording.presenters.isNotEmpty() && recording.presenters.first().id.isNotEmpty()) {
            recordingsObservable = repository.getRecordings(recording.presenters.first().id).toObservable()
        }

        if (recordingsObservable == null) {
            relatedMedia.postValue(emptyList())
            return
        }

        val disposable = recordingsObservable
                .observeOn(rxSchedulers.main)
                .subscribe({
                    relatedMedia.postValue(it)
                }, {
                    Timber.e(it)
                    relatedMedia.postValue(emptyList())
                })

        disposables.add(disposable)
    }

    /**
     * Play / Pause the current [nowPlaying] item
     */
    fun playPauseMedia() {
        nowPlaying.value?.let {
            playMedia(it)
        }
    }

    /**
     * This method takes a [Recording] and does one of the following:
     * - If the item is *not* the active item, then play it directly.
     * - If the item *is* the active item, check whether "pause" is a permitted command. If it is,
     *   then pause playback, otherwise send "play" to resume playback.
     */
    fun playMedia(recording: Recording) {
        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        if (recording.id == nowPlaying?.id) {
            mediaSessionConnection.playbackState.value.let { playbackState ->
                when {
                    playbackState?.isPlaying == true -> transportControls.pause()
                    playbackState?.isPlayEnabled == true -> transportControls.play()
                    else -> {
                        Timber.e("Playable item clicked but neither play nor pause are enabled! (mediaId=${recording.id})")
                    }
                }
            }
        } else {
            transportControls.playFromMediaId(recording.id, null)
        }
    }

    fun skipToPrevious() {
        mediaSessionConnection.transportControls.skipToPrevious()
    }

    fun skipToNext() {
        mediaSessionConnection.transportControls.skipToNext()
    }


    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: List<MediaBrowserCompat.MediaItem>) {
            Timber.d("CHILDREN LOADED: ${children.size}")
        }
    }

    /**
     * When the session's [PlaybackStateCompat] changes, the [playbackState] needs to be updated
     */
    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        val state = it ?: EMPTY_PLAYBACK_STATE
        val metadata = mediaSessionConnection.nowPlaying.value ?: NOTHING_PLAYING

        playbackState.postValue(state)
        findRecording(metadata.id ?: "")
    }

    /**
     * When the session's [MediaMetadataCompat] changes, the [nowPlaying] needs to be updated
     */
    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        val state = mediaSessionConnection.playbackState.value ?: EMPTY_PLAYBACK_STATE
        val metadata = it ?: NOTHING_PLAYING

        findRecording(metadata.id ?: "")
        playbackState.postValue(state)
    }

    private fun findRecording(mediaId: String) {
        val disposable = repository.findRecording(mediaId)
                .observeOn(rxSchedulers.main)
                .subscribe({
                    nowPlaying.postValue(it)
                    thumbedUp.postValue(it.favorite)
                }, {
                    Timber.e(it)
                })

        disposables.add(disposable)
    }

    /**
     * Because there's a complex dance between this [NowPlayingViewModel] and the [MediaSessionConnection]
     * (which is wrapping a [MediaBrowserCompat] object), the usual guidance of using
     * [Transformations] doesn't quite work.
     *
     * Specifically there's three things that are watched that will cause the single piece of
     * [LiveData] exposed from this class to be updated.
     *
     * [subscriptionCallback] (defined above) is called if/when the children of this
     * ViewModel's [mediaId] changes.
     *
     * [mediaSessionConnection#isConnected] is called when [MediaBrowserCompat] is connected meaning we can now
     * interact with its [MediaControllerCompat] to play media.
     *
     * [MediaSessionConnection.playbackState] changes state based on the playback state of
     * the player.
     *
     * [MediaSessionConnection.nowPlaying] changes based on the item that's being played.
     */
    private val mediaSessionConnection: MediaSessionConnection = mediaSessionConnection.also {
        it.subscribe(mediaId, subscriptionCallback)
        it.isConnected.observeForever { connected ->
            if (connected) {
                nowPlaying.value?.let { rec -> playMedia(rec) }
            }
        }

        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
    }

    /**
     * Since we use [LiveData.observeForever] above (in [mediaSessionConnection]), we want
     * to call [LiveData.removeObserver] here to prevent leaking resources when the [NowPlayingViewModel]
     * is not longer in use.
     */
    override fun onCleared() {
        super.onCleared()

        // Remove the permanent observers from the MediaSessionConnection.
        mediaSessionConnection.playbackState.removeObserver(playbackStateObserver)
        mediaSessionConnection.nowPlaying.removeObserver(mediaMetadataObserver)

        // And then, finally, unsubscribe the media ID that was being watched.
        mediaSessionConnection.unsubscribe(mediaId, subscriptionCallback)
    }
}