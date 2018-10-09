package com.tinashe.audioverse.ui.player

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import com.tinashe.audioverse.R
import com.tinashe.audioverse.data.model.*
import com.tinashe.audioverse.injection.ViewModelFactory
import com.tinashe.audioverse.ui.base.BaseActivity
import com.tinashe.audioverse.ui.home.tab.vh.RecordingHolder
import com.tinashe.audioverse.utils.*
import com.tinashe.audioverse.utils.custom.AppBarStateChangeListener
import com.tinashe.audioverse.utils.custom.UniversalAdapter
import kotlinx.android.synthetic.main.activity_now_playing.*
import javax.inject.Inject
import dagger.android.AndroidInjection

class NowPlayingActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: NowPlayingViewModel

    private var shareMenuItem: MenuItem? = null

    private lateinit var recordingsListAdapter: UniversalAdapter<Recording, RecordingHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_now_playing)
        initUI()

        viewModel = getViewModel(this, viewModelFactory)
        viewModel.series.observe(this, Observer {
            recordingsListAdapter.items = it.toMutableList()

            if(it.isEmpty()){
                seriesList.hide()
            } else {
                seriesList.show()
            }
        })

        if (intent.hasExtra(RECORDING)) {
            val rec = intent.getSerializableExtra(RECORDING) as Recording
            initRecording(rec)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_now_playing, menu)
        shareMenuItem = menu?.findItem(R.id.action_share)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initUI() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.overflowIcon?.setTint(Color.WHITE)

        appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                when (state) {
                    State.EXPANDED -> {
                        toolbar.setNavigationIcon(R.drawable.ic_nav_close)
                        toolbar.overflowIcon?.setTint(Color.WHITE)
                        shareMenuItem?.icon?.setTint(Color.WHITE)
                    }
                    State.COLLAPSED -> {
                        toolbar.setNavigationIcon(R.drawable.ic_close_black)
                        toolbar.overflowIcon?.setTint(Color.BLACK)
                        shareMenuItem?.icon?.setTint(Color.BLACK)
                    }
                    State.IDLE -> {
                        // no op
                    }
                }
            }

        })

        recordingsListAdapter = UniversalAdapter(
                { parent, _ -> RecordingHolder.inflate(parent) },
                { vh, _, item ->
                    vh.bind(item, RecordingType.FEATURED, object : RecordingHolder.MoreOptions {
                        override fun play(item: Recording) {

                        }

                        override fun share(content: String) {
                            Helper.shareText(this@NowPlayingActivity, content)
                        }

                        override fun favorite(enabled: Boolean) {
                            //TODO: Implement
                        }

                    })
                }
        )

        seriesList.apply {
            vertical()
            adapter = recordingsListAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initRecording(recording: Recording) {
        var series: Series? = null
        if (recording.series.isNotEmpty()) {
            series = recording.series.first()
        }
        var presenter: Presenter? = null

        if (recording.presenters.isNotEmpty()) {
            presenter = recording.presenters.first()
        }
        val image = series?.photoLarge ?: presenter?.photoLarge ?: ""

        art.loadUrl(image)

        recTitle.text = recording.title
        presenterTxt.text = series?.title ?: presenter?.displayName
        description.text = recording.description

        viewModel.listSeries(recording)
    }

    companion object {
        private const val RECORDING = "arg:recording"
        fun launch(context: Context, recording: Recording) {
            val intent = Intent(context, NowPlayingActivity::class.java)
            intent.putExtra(RECORDING, recording)
            context.startActivity(intent)
        }
    }
}