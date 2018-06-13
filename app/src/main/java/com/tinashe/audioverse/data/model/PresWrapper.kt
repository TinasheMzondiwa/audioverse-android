package com.tinashe.audioverse.data.model

import com.google.gson.annotations.SerializedName

/**
 * Wrapper class to solve our weired api design
 */
class PresWrapper {

    @SerializedName("presenters")
    var recording: Presenter? = null
        get() {
            field?.let {
                it.uri = uri
            }
            return field
        }

    var uri = ""
}