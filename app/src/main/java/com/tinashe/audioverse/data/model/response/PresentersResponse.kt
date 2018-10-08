package com.tinashe.audioverse.data.model.response

import com.google.gson.annotations.SerializedName
import com.tinashe.audioverse.data.model.PresWrapper
import com.tinashe.audioverse.data.model.Presenter

class PresentersResponse : BaseResponse() {

    @SerializedName("result")
    private var result = listOf<PresWrapper>()

    var presenters: List<Presenter> = emptyList()
        get() = result.mapNotNull { it.recording }

}