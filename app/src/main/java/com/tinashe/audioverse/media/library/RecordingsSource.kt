package com.tinashe.audioverse.media.library

import android.content.Context
import android.os.AsyncTask
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tinashe.audioverse.R
import com.tinashe.audioverse.data.database.dao.RecordingsDao
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.media.extensions.*
import com.tinashe.audioverse.utils.glide.GlideApp


class RecordingsSource(context: Context, dao: RecordingsDao) : AbstractMusicSource() {

    private var catalog: List<MediaMetadataCompat> = emptyList()

    init {
        state = STATE_INITIALIZING

        UpdateCatalogTask(GlideApp.with(context)) { mediaItems ->
            catalog = mediaItems
            state = STATE_INITIALIZED
        }.execute(dao)

    }

    override fun iterator(): Iterator<MediaMetadataCompat> = catalog.iterator()
}

private class UpdateCatalogTask(val glide: RequestManager,
                                val listener: (List<MediaMetadataCompat>) -> Unit) :
        AsyncTask<RecordingsDao, Void, List<MediaMetadataCompat>>() {

    override fun doInBackground(vararg params: RecordingsDao): List<MediaMetadataCompat> {

        val recordings = params.first().listAllDirect()

        val mediaItems = ArrayList<MediaMetadataCompat>()

        mediaItems += recordings.asSequence().map { recording ->

            // Block on downloading artwork.
            val art = glide.applyDefaultRequestOptions(glideOptions)
                    .asBitmap()
                    .load(recording.image)
                    .submit(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                    .get()

            MediaMetadataCompat.Builder()
                    .from(recording)
                    .apply {
                        albumArt = art
                    }
                    .build()
        }.toList()

        return mediaItems
    }

    override fun onPostExecute(mediaItems: List<MediaMetadataCompat>) {
        super.onPostExecute(mediaItems)
        listener(mediaItems)
    }

    companion object {
        private const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px

        private val glideOptions = RequestOptions()
                .fallback(R.drawable.default_art)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    }

}

/**
 * Extension method for [MediaMetadataCompat.Builder] to set the fields from
 * our JSON constructed object (to make the code a bit easier to see).
 */
fun MediaMetadataCompat.Builder.from(recording: Recording): MediaMetadataCompat.Builder {

    id = recording.id
    title = recording.title
    artist = recording.presenter
    album = recording.seriesTitle
    duration = recording.duration?.toDouble()?.toLong() ?: 0L
    genre = "" //?
    mediaUri = recording.source
    albumArtUri = recording.image
    flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE

    // To make things easier for *displaying* these, set the display properties as well.
    displayTitle = recording.title
    displaySubtitle = recording.presenter
    displayDescription = recording.description
    displayIconUri = recording.image
    userRating = RatingCompat.newThumbRating(recording.favorite)

    // Add downloadStatus to force the creation of an "extras" bundle in the resulting
    // MediaMetadataCompat object. This is needed to send accurate metadata to the
    // media session during updates.
    downloadStatus = MediaDescriptionCompat.STATUS_NOT_DOWNLOADED

    // Allow it to be used in the typical builder style.
    return this
}