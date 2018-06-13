package com.tinashe.audioverse.data.model.response

import com.google.gson.annotations.SerializedName
import com.tinashe.audioverse.data.model.RecWrapper
import com.tinashe.audioverse.data.model.Recording

class RecordingsResponse : BaseResponse() {

    @SerializedName("result")
    private var result = listOf<RecWrapper>()

    fun getRecordings(): List<Recording> {
        val items = mutableListOf<Recording>()

        result.map {
            it.recording?.let {
                items.add(it)
            }
        }

        return items
    }
}