package com.tinashe.audioverse.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ShareCompat
import com.tinashe.audioverse.data.model.Recording
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

    fun shareText(activity: Activity, content: String){
        val intent = ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setText(content)
                .intent
        intent.resolveActivity(activity.packageManager)?.let {
            activity.startActivity(Intent.createChooser(intent, "Share with..."))
        }
    }

    fun playRecording(context: Context, recording: Recording){
        if (recording.mediaFiles.isEmpty()) {
            Toast.makeText(context, "Missing audio file.", Toast.LENGTH_SHORT).show()

            return
        }
        val intent = Intent()
        intent.action = android.content.Intent.ACTION_VIEW
        intent.setDataAndType(Uri.parse(recording.mediaFiles.first().streamURL), "audio/*")
        intent.resolveActivity(context.packageManager)?.let {
            context.startActivity(Intent.createChooser(intent, "Play with..."))
        }
                ?: Toast.makeText(context, "Missing app to handle streaming.", Toast.LENGTH_SHORT).show()
    }

}