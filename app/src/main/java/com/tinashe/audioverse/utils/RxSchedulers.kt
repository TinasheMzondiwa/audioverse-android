package com.tinashe.audioverse.utils

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

data class RxSchedulers(
        val database: Scheduler = Schedulers.single(),
        val network: Scheduler = Schedulers.io(),
        val main: Scheduler = AndroidSchedulers.mainThread())