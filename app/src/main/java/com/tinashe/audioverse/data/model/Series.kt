package com.tinashe.audioverse.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Series(val id: String) : Serializable {

    var title = ""

    var summary = ""

    var description = ""

    @SerializedName("photo36")
    var photoSmall = ""

    @SerializedName("photo86")
    var photoMed = ""

    @SerializedName("photo256")
    var photoLarge = ""

    var lang = ""
}