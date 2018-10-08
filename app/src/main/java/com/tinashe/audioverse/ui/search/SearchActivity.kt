package com.tinashe.audioverse.ui.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import com.tinashe.audioverse.R
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.injection.ViewModelFactory
import com.tinashe.audioverse.ui.base.BaseActivity
import com.tinashe.audioverse.ui.home.tab.vh.RecordingHolder
import com.tinashe.audioverse.ui.presenter.PresenterActivity
import com.tinashe.audioverse.utils.Helper
import com.tinashe.audioverse.utils.getViewModel
import com.tinashe.audioverse.utils.vertical
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_search.*
import javax.inject.Inject

class SearchActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: SearchViewModel

    private lateinit var listAdapter: SearchListAdapter

    private lateinit var searchQuery: CharSequence

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { bar, offset ->
            bar.isActivated = offset < 0
        })

        viewModel = getViewModel(this, viewModelFactory)
        viewModel.searchResults.observe(this, Observer { result ->
            result?.let {
                listAdapter.results = it
            } ?: listAdapter.reset()
        })

        listAdapter = SearchListAdapter(object : RecordingHolder.MoreOptions {
            override fun play(item: Recording) {
                Helper.playRecording(this@SearchActivity, item)
            }

            override fun share(content: String) {
                Helper.shareText(this@SearchActivity, content)
            }

            override fun favorite(enabled: Boolean) {

            }

        }, { presenter, view ->
            PresenterActivity.view(this, presenter, view)
        }, {
            when (it.first()) {
                is Recording -> SearchResultsActivity.viewRecordings(this, it as ArrayList<Recording>,
                        searchQuery)
                is Presenter -> SearchResultsActivity.viewPresenters(this, it as ArrayList<Presenter>,
                        searchQuery)
            }
        })

        listAdapter.setHasStableIds(true)
        resultsListView.apply {
            vertical()
            adapter = listAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchIcon = menu?.findItem(R.id.action_search)
        val searchView = searchIcon?.actionView as SearchView

        searchIcon.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                onBackPressed()
                return false
            }

        })
        searchIcon.expandActionView()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.search(query)
                searchQuery = query ?: ""
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

}