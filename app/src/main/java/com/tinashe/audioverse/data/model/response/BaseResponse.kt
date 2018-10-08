package com.tinashe.audioverse.data.model.response

open class BaseResponse {

    val message = ""

    val code = 0

    val count = 0

    val success: Boolean
        get() {
            return code == 200
        }
}