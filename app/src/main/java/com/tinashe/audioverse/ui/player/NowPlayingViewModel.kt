package com.tinashe.audioverse.ui.player

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.tinashe.audioverse.R
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.repository.AudioVerseRepository
import com.tinashe.audioverse.media.EMPTY_PLAYBACK_STATE
import com.tinashe.audioverse.media.MediaItemData
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

    var series = MutableLiveData<List<Recording>>()

    private var mediaId = "123"

    override fun subscribe() {

    }

    fun listSeries(recording: Recording) {
        var recordingsObservable: Observable<List<Recording>>? = null
        if (recording.series.isNotEmpty() && recording.seriesId?.isNotEmpty() == true) {
            recordingsObservable = repository.getSeries(recording.seriesId!!)
        } else if (recording.presenters.isNotEmpty() && recording.presenters.first().id.isNotEmpty()) {
            recordingsObservable = repository.getRecordings(recording.presenters.first().id).toObservable()
        }

        if (recordingsObservable == null) {
            series.postValue(emptyList())
            return
        }

        val disposable = recordingsObservable
                .observeOn(rxSchedulers.main)
                .subscribe({
                    series.postValue(it)
                }, {
                    Timber.e(it)
                    series.postValue(emptyList())
                })

        disposables.add(disposable)
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


    /**
     * Use a backing property so consumers of mediaItems only get a [LiveData] instance so
     * they don't inadvertently modify it.
     */
    private val _mediaItems = MutableLiveData<List<MediaItemData>>()
            .apply { postValue(emptyList()) }
    val mediaItems: LiveData<List<MediaItemData>> = _mediaItems

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: List<MediaBrowserCompat.MediaItem>) {
            val itemsList = children.map { child ->
                MediaItemData(child.mediaId!!,
                        child.description.title.toString(),
                        child.description.subtitle.toString(),
                        child.description.iconUri!!,
                        child.isBrowsable,
                        getResourceForMediaId(child.mediaId!!))
            }
            _mediaItems.postValue(itemsList)
        }
    }

    /**
     * When the session's [PlaybackStateCompat] changes, the [mediaItems] need to be updated
     * so the correct [MediaItemData.playbackRes] is displayed on the active item.
     * (i.e.: play/pause button or blank)
     */
    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        val playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metadata = mediaSessionConnection.nowPlaying.value ?: NOTHING_PLAYING
        _mediaItems.postValue(updateState(playbackState, metadata))
    }

    /**
     * When the session's [MediaMetadataCompat] changes, the [mediaItems] need to be updated
     * as it means the currently active item has changed. As a result, the new, and potentially
     * old item (if there was one), both need to have their [MediaItemData.playbackRes]
     * changed. (i.e.: play/pause button or blank)
     */
    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        val playbackState = mediaSessionConnection.playbackState.value ?: EMPTY_PLAYBACK_STATE
        val metadata = it ?: NOTHING_PLAYING
        _mediaItems.postValue(updateState(playbackState, metadata))
    }

    /**
     * Because there's a complex dance between this [ViewModel] and the [MediaSessionConnection]
     * (which is wrapping a [MediaBrowserCompat] object), the usual guidance of using
     * [Transformations] doesn't quite work.
     *
     * Specifically there's three things that are watched that will cause the single piece of
     * [LiveData] exposed from this class to be updated.
     *
     * [subscriptionCallback] (defined above) is called if/when the children of this
     * ViewModel's [mediaId] changes.
     *
     * [MediaSessionConnection.playbackState] changes state based on the playback state of
     * the player, which can change the [MediaItemData.playbackRes]s in the list.
     *
     * [MediaSessionConnection.nowPlaying] changes based on the item that's being played,
     * which can also change the [MediaItemData.playbackRes]s in the list.
     */
    private val mediaSessionConnection: MediaSessionConnection = mediaSessionConnection.also {
        it.subscribe(mediaId, subscriptionCallback)

        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
    }

    /**
     * Since we use [LiveData.observeForever] above (in [mediaSessionConnection]), we want
     * to call [LiveData.removeObserver] here to prevent leaking resources when the [ViewModel]
     * is not longer in use.
     *
     * For more details, see the kdoc on [mediaSessionConnection] above.
     */
    override fun onCleared() {
        super.onCleared()

        // Remove the permanent observers from the MediaSessionConnection.
        mediaSessionConnection.playbackState.removeObserver(playbackStateObserver)
        mediaSessionConnection.nowPlaying.removeObserver(mediaMetadataObserver)

        // And then, finally, unsubscribe the media ID that was being watched.
        mediaSessionConnection.unsubscribe(mediaId, subscriptionCallback)
    }

    private fun getResourceForMediaId(mediaId: String): Int {
        val isActive = mediaId == mediaSessionConnection.nowPlaying.value?.id
        val isPlaying = mediaSessionConnection.playbackState.value?.isPlaying ?: false
        return when {
            !isActive -> NO_RES
            isPlaying -> R.drawable.ic_pause
            else -> R.drawable.ic_play
        }
    }

    private fun updateState(playbackState: PlaybackStateCompat,
                            mediaMetadata: MediaMetadataCompat): List<MediaItemData> {

        val newResId = when (playbackState.isPlaying) {
            true -> R.drawable.ic_pause
            else -> R.drawable.ic_play
        }

        return mediaItems.value?.map {
            val useResId = if (it.mediaId == mediaMetadata.id) newResId else NO_RES
            it.copy(playbackRes = useResId)
        } ?: emptyList()
    }

    companion object {
        private const val NO_RES = 0
    }
}