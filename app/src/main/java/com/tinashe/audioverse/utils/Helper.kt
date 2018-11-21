package com.tinashe.audioverse.utils

import android.app.Activity
import android.content.Intent
import androidx.core.app.ShareCompat
import com.tinashe.audioverse.data.model.RecordingType
import java.text.SimpleDateFormat
import java.util.*

object Helper {

    private const val DAY_FORMAT = "dd-MM-yyyy"

    fun formatDuration(duration: String): String {
        if (duration.isEmpty()) {
            return ""
        }

        return formatDuration(duration.toDouble())
    }

    fun formatDuration(duration: Double): String {

        val hours = duration.div(3600).toInt()
        val minutes = (duration % 3600).div(60).toInt()
        val seconds = (duration % 60).toInt()

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    /**
     * Classify a recording based on the day it was fetched from api
     */
    fun getTag(type: RecordingType): String {
        val day = SimpleDateFormat(DAY_FORMAT, Locale.getDefault()).format(Calendar.getInstance().time)
        return "${type.name}-$day"
    }

    fun shareText(activity: Activity, content: String) {
        val intent = ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setText(content)
                .intent
        intent.resolveActivity(activity.packageManager)?.let {
            activity.startActivity(Intent.createChooser(intent, "Share with..."))
        }
    }

}