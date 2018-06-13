package com.tinashe.audioverse.utils.custom

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Source: https://github.com/radityagumay/universal-adapter
 **/

interface Universal<in T> {
    fun addAll(items: List<T>)

    fun add(item: T)

    fun update(item: T)

    fun updateRange(vararg items: T)

    fun remove(item: T)

    fun removeRange(vararg items: T)
}

class UniversalAdapter<T, VH : RecyclerView.ViewHolder>(
        private val onCreateViewHolder: (ViewGroup, Int) -> VH,
        private val onBindViewHolder: (VH, Int, T) -> Unit,
        private val onViewType: ((Int) -> Int)? = null) : RecyclerView.Adapter<VH>(),
        Universal<T> {

    var items = mutableListOf<T>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            onCreateViewHolder.invoke(parent, viewType)

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder.invoke(holder, position, items[position])
    }

    override fun getItemViewType(position: Int) = onViewType?.invoke(position) ?: super.getItemViewType(position)

    override fun getItemCount() = items.size

    override fun addAll(items: List<T>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun add(item: T) {
        this.items.add(item)
        notifyItemInserted(this.items.size)
    }

    override fun remove(item: T) {
        this.items.remove(item)
    }

    override fun removeRange(vararg items: T) {
        items.forEach { remove(it) }
    }

    override fun update(item: T) {
        items.forEachIndexed { index, i ->
            if (i == item) {
                items[index] = item
            }
        }
    }

    override fun updateRange(vararg items: T) {
        for (i in 0 until this.items.size) {
            (0 until items.size)
                    .filter { i == it }
                    .forEach { this.items[i] = items[it] }
        }
    }
}