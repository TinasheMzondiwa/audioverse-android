package com.tinashe.audioverse.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.audioverse.R
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.model.RecordingType
import com.tinashe.audioverse.ui.base.BaseActivity
import com.tinashe.audioverse.ui.home.tab.vh.PresenterHolder
import com.tinashe.audioverse.ui.home.tab.vh.RecordingHolder
import com.tinashe.audioverse.ui.player.NowPlayingActivity
import com.tinashe.audioverse.ui.presenter.PresenterActivity
import com.tinashe.audioverse.utils.Helper
import com.tinashe.audioverse.utils.custom.UniversalAdapter
import com.tinashe.audioverse.utils.vertical
import kotlinx.android.synthetic.main.activity_search_results.*


class SearchResultsActivity : BaseActivity() {

    private lateinit var recordingsAdapter: UniversalAdapter<Recording, RecordingHolder>
    private lateinit var presentersAdapter: UniversalAdapter<Presenter, PresenterHolder>

    private val appBarElevation = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val raiseTitleBar = dy > 0 || recyclerView.computeVerticalScrollOffset() != 0
            appbar.isActivated = raiseTitleBar
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        when {
            intent.hasExtra(ARG_PRESENTERS) -> {

                setTitle(R.string.presenters)

                val list = intent.getSerializableExtra(ARG_PRESENTERS) as ArrayList<Presenter>

                presentersAdapter = UniversalAdapter(
                        { parent, _ -> PresenterHolder.inflateSearch(parent) },
                        { vh, _, item -> vh.bind(item) { presenter, view -> PresenterActivity.view(this, presenter, view) } }
                )
                presentersAdapter.items = list
                resultsListView.apply {
                    vertical()
                    adapter = presentersAdapter
                }

            }
            intent.hasExtra(ARG_RECORDINGS) -> {

                setTitle(R.string.presentations)

                val list = intent.getSerializableExtra(ARG_RECORDINGS) as ArrayList<Recording>
                recordingsAdapter = UniversalAdapter(
                        { parent, _ -> RecordingHolder.inflate(parent) },
                        { vh, _, item ->
                            vh.bind(item, RecordingType.NEW, object : RecordingHolder.MoreOptions {
                                override fun play(item: Recording) {
                                    NowPlayingActivity.launch(this@SearchResultsActivity, item)
                                }

                                override fun share(content: String) {
                                    Helper.shareText(this@SearchResultsActivity, content)
                                }

                                override fun favorite(enabled: Boolean) {
                                    //TODO: Implement
                                }

                            })
                        }
                )

                recordingsAdapter.items = list
                resultsListView.apply {
                    vertical()
                    adapter = recordingsAdapter
                }
            }
            else -> {
                finish()
                return
            }
        }

        resultsListView.addOnScrollListener(appBarElevation)
        intent.getCharSequenceExtra(ARG_QUERY)?.let {
            title = "'$it' in $title"
        }

    }

    companion object {

        private const val ARG_RECORDINGS = "ARG_RECORDINGS"
        private const val ARG_PRESENTERS = "ARG_PRESENTERS"
        private const val ARG_QUERY = "ARG_QUERY"

        fun viewPresenters(activity: Activity, presenters: ArrayList<Presenter>, query: CharSequence) {
            val intent = Intent(activity, SearchResultsActivity::class.java)
            intent.putExtra(ARG_PRESENTERS, presenters)
            intent.putExtra(ARG_QUERY, query)
            activity.startActivity(intent)
        }

        fun viewRecordings(activity: Activity, recordings: ArrayList<Recording>, query: CharSequence) {
            val intent = Intent(activity, SearchResultsActivity::class.java)
            intent.putExtra(ARG_RECORDINGS, recordings)
            intent.putExtra(ARG_QUERY, query)
            activity.startActivity(intent)
        }
    }
}