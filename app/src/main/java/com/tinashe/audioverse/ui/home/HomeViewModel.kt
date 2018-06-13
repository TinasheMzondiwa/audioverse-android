package com.tinashe.audioverse.ui.home

import com.tinashe.audioverse.ui.base.RxViewModel
import com.tinashe.audioverse.ui.base.SingleLiveEvent
import javax.inject.Inject

class HomeViewModel @Inject constructor() : RxViewModel() {

    var navigationHolder = SingleLiveEvent<Int>()

    init {
        navigationHolder.value = Navigation.PRESENTATIONS
    }

    override fun subscribe() {

    }

    fun switchNavigation(@Navigation item: Int){
        navigationHolder.value = item
    }
}