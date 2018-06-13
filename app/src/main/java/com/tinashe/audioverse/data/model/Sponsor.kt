package com.tinashe.audioverse.data.model

import com.google.gson.annotations.SerializedName

data class Sponsor(val id: String) {

    var title = ""

    var description = ""

    var location = ""

    @SerializedName("photo36")
    var photoSmall = ""

    @SerializedName("photo86")
    var photoMed = ""

    @SerializedName("photo256")
    var photoLarge = ""

    var lang = ""
}