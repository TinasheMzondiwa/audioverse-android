package com.tinashe.audioverse.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

@Entity(tableName = "recordings", indices = [(Index(value = ["title"])), (Index(value = ["description"]))])
data class Recording(
        @PrimaryKey
        val id: String) : SearchItem, Serializable {

    var sponsorId: String? = ""

    var conferenceId: String? = ""

    var conferenceTitle: String? = ""

    var seriesId: String? = ""

    var seriesTitle: String? = ""

    var copyRightYear: String? = ""

    var title: String? = ""

    var description: String? = ""

    var recordingDate: Date? = null

    var publishDate: Date? = null

    var created: Date? = null

    var duration: String? = ""

    var sponsor = listOf<Sponsor>()

    var mediaFiles = listOf<MediaFile>()

    var presenters = listOf<Presenter>()

    @Ignore
    var presenter: String = ""
        get() {
            if (field.isNotEmpty()) {
                return field
            }

            field =  if (presenters.isNotEmpty()) {
                presenters.first().displayName
            } else
                ""

            return field
        }

    @Ignore
    var source: String = ""
        get() {
            if (field.isNotEmpty()) {
                return field
            }

            field = if (mediaFiles.isNotEmpty()) {
                mediaFiles.first().streamURL
            } else
                ""

            return field
        }

    @Ignore
    var image: String = ""
        get() {
            if (field.isNotEmpty()) {
                return field
            }
            var series: Series? = null
            if (this.series.isNotEmpty()) {
                series = this.series.first()
            }
            var presenter: Presenter? = null

            if (this.presenters.isNotEmpty()) {
                presenter = this.presenters.first()
            }

            field = series?.photoMed ?: presenter?.photoMed ?: ""

            return field
        }

    var series = listOf<Series>()

    var shareUrl: String? = ""

    var downloadDisabled: String? = ""

    var ad: String? = ""

    @SerializedName("photo36")
    var photoSmall: String? = ""

    @SerializedName("photo86")
    var photoMed: String? = ""

    @SerializedName("photo256")
    var photoLarge: String? = ""

    var lang: String? = ""

    var uri: String? = ""

    var tag: String? = null
}