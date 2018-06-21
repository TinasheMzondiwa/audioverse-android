package com.tinashe.audioverse.data.model

import androidx.room.Entity
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