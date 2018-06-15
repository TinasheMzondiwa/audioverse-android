package com.tinashe.audioverse.ui.home.tab.vh

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tinashe.audioverse.R
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.utils.custom.PicassoCircleTransform
import com.tinashe.audioverse.utils.inflateView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.presenter_item.*

class PresenterHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    companion object {
        fun inflate(parent: ViewGroup):
                PresenterHolder = PresenterHolder(inflateView(R.layout.presenter_item, parent, false))
    }

    fun bind(presenter: Presenter, callback: (Presenter, View) -> Unit) {
        name.text = presenter.displayName
        count.text = presenter.recordingCount.toString()

        Picasso.get()
                .load(presenter.photoMed)
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .transform(PicassoCircleTransform())
                .into(avatar)

        itemView.setOnClickListener {
            callback.invoke(presenter, avatar)
        }
    }
}