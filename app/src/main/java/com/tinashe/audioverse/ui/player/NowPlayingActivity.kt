package com.tinashe.audioverse.ui.player

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import com.tinashe.audioverse.R
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.model.Series
import com.tinashe.audioverse.data.model.State
import com.tinashe.audioverse.injection.ViewModelFactory
import com.tinashe.audioverse.media.extensions.isSkipToNextEnabled
import com.tinashe.audioverse.media.extensions.isSkipToPreviousEnabled
import com.tinashe.audioverse.ui.base.BaseActivity
import com.tinashe.audioverse.ui.home.tab.vh.RecordingHolder
import com.tinashe.audioverse.utils.*
import com.tinashe.audioverse.utils.custom.AppBarStateChangeListener
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_now_playing.*
import kotlinx.android.synthetic.main.include_player_vew.*
import kotlinx.android.synthetic.main.include_seekbar.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NowPlayingActivity : BaseActivity(), RecordingHolder.MoreOptions {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: NowPlayingViewModel

    private var shareMenuItem: MenuItem? = null

    private lateinit var relatedListAdapter: RelatedListAdapter

    private val executorService = Executors.newSingleThreadScheduledExecutor()
    private var scheduleFuture: ScheduledFuture<*>? = null
    private val updateProgressTask = Runnable { updateProgress() }
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_now_playing)
        initUI()

        viewModel = getViewModel(this, viewModelFactory)
        viewModel.relatedMedia.observe(this, Observer {
            relatedListAdapter.items = ArrayList(it)

            if (it.isEmpty()) {
                seriesList.hide()
            } else {
                seriesList.show()
            }
        })

        viewModel.nowPlaying.observe(this, Observer {
            initRecording(it)
        })
        viewModel.playbackState.observe(this, Observer {
            fab.setImageResource(when (it.state) {
                PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_BUFFERING -> R.drawable.ic_pause
                else -> R.drawable.ic_play
            })

            fab.isEnabled = it.state != PlaybackStateCompat.STATE_BUFFERING

            playerPrevious.visibility = if (it.isSkipToPreviousEnabled) View.VISIBLE else View.INVISIBLE
            playerNext.visibility = if (it.isSkipToNextEnabled) View.VISIBLE else View.INVISIBLE

            when (it.state) {
                PlaybackStateCompat.STATE_PLAYING -> scheduleSeekBarUpdate()
                else -> stopSeekBarUpdate()
            }
        })
        viewModel.thumbedUp.observe(this, Observer { thumbedUp ->

            when (thumbedUp) {
                true -> {
                    playerThumbsUp.setImageResource(R.drawable.round_thumb_up)
                    playerThumbsDown.setImageResource(R.drawable.outline_thumb_down)
                }
                false -> {
                    playerThumbsUp.setImageResource(R.drawable.outline_thumb_up)
                    playerThumbsDown.setImageResource(R.drawable.round_thumb_down)
                }
                else -> {
                    playerThumbsUp.setImageResource(R.drawable.outline_thumb_up)
                    playerThumbsDown.setImageResource(R.drawable.outline_thumb_down)
                }
            }
        })

        if (intent.hasExtra(RECORDING)) {
            val rec = intent.getSerializableExtra(RECORDING) as Recording
            viewModel.setMediaItem(rec)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_now_playing, menu)
        shareMenuItem = menu?.findItem(R.id.action_share)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_share -> {
                viewModel.nowPlaying.value?.let {
                    val content = "${it.title}\n\n${it.shareUrl}"

                    Helper.shareText(this, content)
                }
                true
            }
            R.id.action_add_to_list -> {
                snackbar.show(messageId = R.string.coming_soon, longDuration = true)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

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

        relatedListAdapter = RelatedListAdapter(this)

        seriesList.apply {
            vertical()
            adapter = relatedListAdapter
        }

        fab.setOnClickListener { viewModel.playPauseMedia() }
        playerPrevious.setOnClickListener { viewModel.skipToPrevious() }
        playerNext.setOnClickListener { viewModel.skipToNext() }

        playerThumbsDown.setOnClickListener { viewModel.thumbDownMedia() }
        playerThumbsUp.setOnClickListener { viewModel.thumbUpMedia() }

        playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var userSelectedPosition = 0
            var userIsSeeking = false

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    userSelectedPosition = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                userIsSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                userIsSeeking = false

                viewModel.seekToPosition(userSelectedPosition * PROGRESS_UPDATE_INTERNAL)
            }
        })
        makeOfflinePin.setOnClickListener {
            snackbar.show(messageId = R.string.coming_soon, longDuration = true)
        }
    }

    override fun play(item: Recording) {
        viewModel.playMedia(item)
    }

    override fun share(content: String) {
        Helper.shareText(this@NowPlayingActivity, content)
    }

    override fun favorite(enabled: Boolean) {
        //TODO: Implement
    }

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

        art.loadUrl(image, R.color.theme)

        recTitle.text = recording.title
        presenterTxt.text = if (series?.title?.isNotEmpty() == true) {
            "${presenter?.displayName}\n${series.title}"
        } else {
            presenter?.displayName
        }
        description.text = recording.description

        trackProgressTime.text = Helper.formatDuration(0.0)
        trackProgressEnd.text = Helper.formatDuration(recording.duration ?: "")
        playerSeekBar.max = recording.duration?.toDouble()?.toInt() ?: 0

        viewModel.listRelated(recording)
    }

    public override fun onDestroy() {
        super.onDestroy()
        stopSeekBarUpdate()
        executorService.shutdown()
    }

    private fun stopSeekBarUpdate() {
        scheduleFuture?.cancel(false)
    }

    private fun scheduleSeekBarUpdate() {
        stopSeekBarUpdate()
        if (!executorService.isShutdown) {
            scheduleFuture = executorService.scheduleAtFixedRate(
                    { handler.post(updateProgressTask) }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS)
        }
    }

    private fun updateProgress() {

        val playbackState: PlaybackStateCompat = viewModel.playbackState.value ?: return

        var currentPosition = playbackState.position

        if (playbackState.state != PlaybackStateCompat.STATE_PAUSED) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensures that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            val timeDelta = SystemClock.elapsedRealtime() - playbackState.lastPositionUpdateTime

            currentPosition += (timeDelta * playbackState.playbackSpeed).toLong()

        } else {
            return
        }

        val seconds = currentPosition / PROGRESS_UPDATE_INTERNAL
        playerSeekBar.progress = seconds.toInt()

        val progress = Helper.formatDuration(seconds.toDouble())
        if (progress.isNotEmpty()) {
            trackProgressTime.text = progress
        }
    }


    companion object {
        private const val RECORDING = "arg:recording"

        private const val PROGRESS_UPDATE_INTERNAL: Long = 1000
        private const val PROGRESS_UPDATE_INITIAL_INTERVAL: Long = 100

        fun launch(context: Context, recording: Recording) {
            val intent = Intent(context, NowPlayingActivity::class.java)
            intent.putExtra(RECORDING, recording)
            context.startActivity(intent)
        }
    }
}