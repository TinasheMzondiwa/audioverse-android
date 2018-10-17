package com.tinashe.audioverse.data.repository.helper

import android.content.Context
import android.net.ConnectivityManager

class NetworkHelper constructor(private val context: Context){

    fun hasConnection(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnected
    }
}