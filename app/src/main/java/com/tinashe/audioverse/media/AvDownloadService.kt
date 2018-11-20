package com.tinashe.audioverse.media

import android.app.Notification
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadManager.TaskState
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationUtil
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util
import com.tinashe.audioverse.AudioVerseApp
import com.tinashe.audioverse.R


class AvDownloadService : DownloadService(FOREGROUND_NOTIFICATION_ID) {

    override fun getDownloadManager(): DownloadManager {
        return (this.application as AudioVerseApp).downloadManager
    }

    override fun getForegroundNotification(taskStates: Array<out DownloadManager.TaskState>?): Notification {
        return DownloadNotificationUtil.buildProgressNotification(
                /* context= */ this,
                R.drawable.exo_controls_play,
                CHANNEL_ID,
                /* contentIntent= */ null,
                /* message= */ null,
                taskStates)
    }

    override fun getScheduler(): Scheduler? {
        return PlatformScheduler(this, JOB_ID)
    }

    override fun onTaskStateChanged(taskState: TaskState?) {
        if (taskState!!.action.isRemoveAction) {
            return
        }
        var notification: Notification? =
                /* contentIntent= */ null
        if (taskState.state == TaskState.STATE_COMPLETED) {
            notification = DownloadNotificationUtil.buildDownloadCompletedNotification(
                    /* context= */ this,
                    R.drawable.exo_controls_play,
                    CHANNEL_ID, null,
                    Util.fromUtf8Bytes(taskState.action.data))/* contentIntent= */
        } else if (taskState.state == TaskState.STATE_FAILED) {
            notification = DownloadNotificationUtil.buildDownloadFailedNotification(
                    /* context= */ this,
                    R.drawable.exo_controls_play,
                    CHANNEL_ID, null,
                    Util.fromUtf8Bytes(taskState.action.data))
        }
        val notificationId = FOREGROUND_NOTIFICATION_ID + 1 + taskState.taskId
        NotificationUtil.setNotification(this, notificationId, notification)
    }

    companion object {
        private const val CHANNEL_ID = "download_channel"
        private const val JOB_ID = 1
        private const val FOREGROUND_NOTIFICATION_ID = 1
    }
}