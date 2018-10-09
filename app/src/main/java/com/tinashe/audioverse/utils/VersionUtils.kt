package com.tinashe.audioverse.utils

import android.os.Build

object VersionUtils {

    fun isAtleastO(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }
}