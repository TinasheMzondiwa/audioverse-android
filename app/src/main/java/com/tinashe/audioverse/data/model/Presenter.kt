package com.tinashe.audioverse.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "presenters", indices = [(Index(value = ["displayName"]))])
data class Presenter(
        @PrimaryKey
        val id: String) : Serializable {

    var givenName = ""

    var surname = ""

    var displayName: String = ""
        get() = "$givenName $surname".trim()

    var summary = ""

    var description = ""

    var website = ""

    var recordingCount = 0

    @SerializedName("photo36")
    var photoSmall = ""

    @SerializedName("photo86")
    var photoMed = ""

    @SerializedName("photo256")
    var photoLarge = ""

    var lang = ""

    var uri = ""
}