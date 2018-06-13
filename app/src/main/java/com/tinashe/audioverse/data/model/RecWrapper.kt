package com.tinashe.audioverse.data.model

import com.google.gson.annotations.SerializedName

/**
 * Wrapper class to solve our weired api design
 */
class RecWrapper {

    @SerializedName("recordings")
    var recording: Recording? = null
        get() {
            field?.let {
                it.uri = uri
            }
            return field
        }

    var uri = ""
}