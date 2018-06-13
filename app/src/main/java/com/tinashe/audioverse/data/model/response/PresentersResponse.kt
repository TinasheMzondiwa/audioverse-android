package com.tinashe.audioverse.data.model.response

import com.google.gson.annotations.SerializedName
import com.tinashe.audioverse.data.model.PresWrapper
import com.tinashe.audioverse.data.model.Presenter

class PresentersResponse : BaseResponse() {

    @SerializedName("result")
    private var result = listOf<PresWrapper>()

    fun getPresenterss(): List<Presenter> {
        val items = mutableListOf<Presenter>()

        result.map {
            it.recording?.let {
                items.add(it)
            }
        }

        return items
    }
}