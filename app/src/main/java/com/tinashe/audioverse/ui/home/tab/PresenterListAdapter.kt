package com.tinashe.audioverse.ui.home.tab

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.ui.home.tab.vh.PresenterHolder

class PresenterListAdapter constructor(private val callback: (Presenter, View) -> Unit) : PagedListAdapter<Presenter, PresenterHolder>(PRESENTER_COMPARATOR),
        FastScrollRecyclerView.SectionedAdapter {

    override fun getItemId(position: Int): Long {
        return getItem(position)?.id?.toLong() ?: super.getItemId(position)
    }

    override fun getSectionName(position: Int): String {
        return getItem(position)?.let {
            it.displayName[0].toString().trim()
        } ?: ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresenterHolder = PresenterHolder.inflate(parent)

    override fun onBindViewHolder(holder: PresenterHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, callback)
        }
    }

    companion object {

        val PRESENTER_COMPARATOR = object : DiffUtil.ItemCallback<Presenter>() {
            override fun areContentsTheSame(oldItem: Presenter, newItem: Presenter): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: Presenter, newItem: Presenter): Boolean =
                    oldItem.id == newItem.id
        }
    }
}