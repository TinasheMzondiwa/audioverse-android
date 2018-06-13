package com.tinashe.audioverse.utils

import com.tinashe.audioverse.data.model.RecordingType
import java.text.SimpleDateFormat
import java.util.*

object Helper {

    private const val DAY_FORMAT = "dd-mm-yyy"

    fun formatDuration(duration: String): String {
        if (duration.isEmpty()) {
            return ""
        }
        val total = duration.toDouble()
        val hours = total.div(3600).toInt()
        val minutes = (total % 3600).div(60).toInt()
        val seconds = (total % 60).toInt()

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     * Classify a recording based on the day it was fetched from api
     */
    fun getTag(type: RecordingType): String {
        val day = SimpleDateFormat(DAY_FORMAT, Locale.getDefault()).format(Calendar.getInstance().time)
        return "${type.name}-$day"
    }

}