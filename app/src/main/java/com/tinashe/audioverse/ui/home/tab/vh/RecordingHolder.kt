package com.tinashe.audioverse.ui.home.tab.vh

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
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

    fun bind(recording: Recording, type: RecordingType, options: MoreOptions) {
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
            options.play(recording)
        }

        more.setOnClickListener {

            val menu = PopupMenu(it.context, it)
            menu.inflate(R.menu.menu_more)
            recording.shareUrl?.let {
                menu.menu.findItem(R.id.action_share).isVisible = true
            }
            menu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_play -> itemView.performClick()
                    R.id.action_share -> options.share("${recording.title}\n\n${recording.shareUrl!!}")
                }
                return@setOnMenuItemClickListener true
            }
            menu.show()
        }
    }

    interface MoreOptions {
        fun play(item: Recording)

        fun share(content: String)

        fun favorite(enabled: Boolean)
    }
}