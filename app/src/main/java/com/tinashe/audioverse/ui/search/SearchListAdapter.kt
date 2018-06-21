package com.tinashe.audioverse.ui.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.audioverse.data.model.*
import com.tinashe.audioverse.ui.home.tab.vh.PresenterHolder
import com.tinashe.audioverse.ui.home.tab.vh.RecordingHolder

class SearchListAdapter constructor(private val recording: RecordingHolder.MoreOptions,
                                    private val presenter: (Presenter, View) -> Unit,
                                    private val viewAll: (ArrayList<SearchItem>) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = mutableListOf<SearchItem>()

    var results: SearchResult = SearchResult(emptyList(), emptyList())
        set(value) {
            field = value

            var sermons = value.recordings
            var people = value.presenters

            if (sermons.size > MAX_GROUPING && people.isNotEmpty()) {
                sermons = sermons.subList(0, MAX_GROUPING)
            }

            if (people.size > MAX_GROUPING && sermons.isNotEmpty()) {
                people = people.subList(0, MAX_GROUPING)
            }

            items = mutableListOf()
            items.apply {
                if (sermons.isNotEmpty()) {
                    add(SearchItemHeader(PRESENTATIONS, Recording::class.java))
                }
                addAll(sermons)

                if (people.isNotEmpty()) {
                    add(SearchItemHeader(PRESENTERS, Presenter::class.java))
                }
                addAll(people)
            }

            notifyDataSetChanged()
        }

    override fun getItemId(position: Int): Long {
        return items[position].hashCode().toLong()
    }

    override fun getItemViewType(position: Int): Int {

        val item = items[position]

        return when (item) {
            is Recording -> TYPE_SERMONS
            is Presenter -> TYPE_PEOPLE
            is SearchItemHeader -> TYPE_HEADER
            else -> TYPE_UNKNOWN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SERMONS -> RecordingHolder.inflate(parent)
            TYPE_PEOPLE -> PresenterHolder.inflateSearch(parent)
            TYPE_HEADER -> SearchHeaderHolder.inflate(parent)
            else -> throw RuntimeException("Unsupported Type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is RecordingHolder -> holder.bind(item as Recording, RecordingType.NEW, recording)
            is PresenterHolder -> holder.bind(item as Presenter, presenter)
            is SearchHeaderHolder -> {

                val more = when ((item as SearchItemHeader).type) {
                    Recording::class.java -> {
                        if (results.presenters.isEmpty()) {
                            0
                        } else {
                            results.recordings.size - MAX_GROUPING
                        }
                    }
                    Presenter::class.java -> {
                        if (results.recordings.isEmpty()) {
                            0
                        } else {
                            results.presenters.size - MAX_GROUPING
                        }
                    }
                    else -> 0
                }

                holder.bind(item, more) {

                    if (item.title == PRESENTATIONS) {
                        viewAll.invoke(ArrayList(results.recordings))
                    } else if (item.title == PRESENTERS) {
                        viewAll.invoke(ArrayList(results.presenters))
                    }
                }
            }
            else -> return
        }
    }

    fun reset() {
        results = SearchResult(emptyList(), emptyList())
    }

    companion object {
        private const val TYPE_UNKNOWN = -1
        private const val TYPE_SERMONS = 1
        private const val TYPE_PEOPLE = 2
        private const val TYPE_HEADER = 3

        private const val MAX_GROUPING = 5

        private const val PRESENTERS = "Presenters"
        private const val PRESENTATIONS = "Presentations"
    }
}