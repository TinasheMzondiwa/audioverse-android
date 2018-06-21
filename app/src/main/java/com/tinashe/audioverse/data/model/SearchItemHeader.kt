package com.tinashe.audioverse.data.model

data class SearchItemHeader(val title: String, val type: Class<out SearchItem>) : SearchItem