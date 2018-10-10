package com.tinashe.audioverse.ui.player

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.model.RecordingType
import com.tinashe.audioverse.ui.home.tab.vh.RecordingHolder

class RelatedListAdapter(private val options: RecordingHolder.MoreOptions) :
        RecyclerView.Adapter<RecordingHolder>() {

    var items = arrayListOf<Recording>()
        set(value) {
            val callback = MediaDiffCallback(field, value)
            val diffResult = DiffUtil.calculateDiff(callback)

            field.clear()
            field.addAll(value)

            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingHolder =
            RecordingHolder.inflate(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecordingHolder, position: Int) {
        val recording = items[position]

        holder.bind(recording, RecordingType.FEATURED, options)
    }
}