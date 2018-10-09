package com.tinashe.audioverse.ui.home.tab.vh

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.audioverse.R
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.utils.inflateView
import com.tinashe.audioverse.utils.loadAvatar
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.presenter_item.*

class PresenterHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    companion object {
        fun inflate(parent: ViewGroup):
                PresenterHolder = PresenterHolder(inflateView(R.layout.presenter_item, parent, false))

        fun inflateSearch(parent: ViewGroup):
                PresenterHolder = PresenterHolder(inflateView(R.layout.presenter_item_full, parent, false))
    }

    fun bind(presenter: Presenter, callback: (Presenter, View) -> Unit) {
        name.text = presenter.displayName
        count.text = presenter.recordingCount.toString()

        avatar.loadAvatar(presenter.photoMed)

        itemView.setOnClickListener {
            callback.invoke(presenter, avatar)
        }
    }
}