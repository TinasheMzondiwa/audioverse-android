package com.tinashe.audioverse.data.model

import java.io.Serializable

data class MediaFile(val fileId: String) : Serializable {

    val fileName: String = ""

    val filesize: String = ""

    val duration: String = ""

    val streamURL: String = ""

    val downloadURL: String = ""
}