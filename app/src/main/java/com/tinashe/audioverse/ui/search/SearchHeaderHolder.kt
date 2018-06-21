package com.tinashe.audioverse.ui.search

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.audioverse.R
import com.tinashe.audioverse.data.model.SearchItemHeader
import com.tinashe.audioverse.utils.hide
import com.tinashe.audioverse.utils.inflateView
import com.tinashe.audioverse.utils.show
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_header_item.*

class SearchHeaderHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    companion object {
        fun inflate(parent: ViewGroup):
                SearchHeaderHolder = SearchHeaderHolder(inflateView(R.layout.search_header_item, parent, false))
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: SearchItemHeader, more: Int, moreClick: () -> Unit) {
        title.text = item.title

        if (more > 0) {
            btnMore.text = "$more more"
            btnMore.show()
        } else {
            btnMore.hide()
        }

        btnMore.setOnClickListener { moreClick.invoke() }
    }
}