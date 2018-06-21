package com.tinashe.audioverse.data.model

data class SearchResult(var recordings: List<Recording>, var presenters: List<Presenter>){
    override fun toString(): String {
        return "SearchResult(recordings=${recordings.size}, presenters=${presenters.size})"
    }
}