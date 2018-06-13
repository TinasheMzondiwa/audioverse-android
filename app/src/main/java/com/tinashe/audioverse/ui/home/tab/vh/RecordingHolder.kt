package com.tinashe.audioverse.ui.home.tab.vh

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tinashe.audioverse.R
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.model.RecordingType
import com.tinashe.audioverse.data.model.Series
import com.tinashe.audioverse.utils.Helper
import com.tinashe.audioverse.utils.custom.PicassoCircleTransform
import com.tinashe.audioverse.utils.hide
import com.tinashe.audioverse.utils.inflateView
import com.tinashe.audioverse.utils.show
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.recording_item.*


class RecordingHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    companion object {
        fun inflate(parent: ViewGroup):
                RecordingHolder = RecordingHolder(inflateView(R.layout.recording_item, parent, false))
    }

    fun bind(recording: Recording, type: RecordingType) {
        title.text = recording.title

        var series: Series? = null
        if (recording.series.isNotEmpty()) {
            series = recording.series.first()
        }
        var presenter: Presenter? = null

        if (recording.presenters.isNotEmpty()) {
            presenter = recording.presenters.first()
        }
        val image = series?.photoMed ?: presenter?.photoMed ?: ""

        Picasso.get()
                .load(image)
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .transform(PicassoCircleTransform())
                .into(avatar)

        if (presenter != null && type != RecordingType.PRESENTER) {
            seriesName.text = presenter.displayName
            seriesName.show()
        } else {
            series?.let {
                seriesName.show()
                seriesName.text = it.title
            } ?: seriesName.hide()
        }



        duration.text = Helper.formatDuration(recording.duration ?: "")

        itemView.setOnClickListener {
            val context = it.context
            if (recording.mediaFiles.isEmpty()) {
                Toast.makeText(context, "Missing audio file.", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }
            val intent = Intent()
            intent.action = android.content.Intent.ACTION_VIEW
            intent.setDataAndType(Uri.parse(recording.mediaFiles.first().streamURL), "audio/*")
            intent.resolveActivity(context.packageManager)?.let {
                context.startActivity(intent)
            }
                    ?: Toast.makeText(context, "Missing app to handle streaming.", Toast.LENGTH_SHORT).show()

        }
    }
}