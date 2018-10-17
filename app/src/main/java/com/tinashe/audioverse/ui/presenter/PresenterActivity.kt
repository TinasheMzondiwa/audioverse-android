package com.tinashe.audioverse.ui.presenter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import com.tinashe.audioverse.R
import com.tinashe.audioverse.data.model.BundledExtras
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.model.RecordingType
import com.tinashe.audioverse.injection.ViewModelFactory
import com.tinashe.audioverse.ui.base.BaseActivity
import com.tinashe.audioverse.ui.home.tab.vh.RecordingHolder
import com.tinashe.audioverse.ui.player.NowPlayingActivity
import com.tinashe.audioverse.utils.*
import com.tinashe.audioverse.utils.custom.UniversalAdapter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_presenter.*
import javax.inject.Inject

class PresenterActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: PresenterViewModel

    private lateinit var listAdapter: UniversalAdapter<Recording, RecordingHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_presenter)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!intent.hasExtra(BundledExtras.PRESENTER)) {
            finish()
            return
        }

        val presenter = intent.getSerializableExtra(BundledExtras.PRESENTER) as Presenter
        initPresenter(presenter)

        viewModel = getViewModel(this, viewModelFactory)
        viewModel.presentations.observe(this, Observer { list ->

            list?.let {
                if (it.isNotEmpty()) {
                    presentationsLabel.show()
                } else {
                    presentationsLabel.hide()
                }

                listAdapter.items = it.toMutableList()

                progressBar.hide()
            }
        })
        viewModel.fetchPresentations(presenter.id)

    }

    @SuppressLint("SetTextI18n")
    private fun initPresenter(presenter: Presenter) {
        ViewCompat.setTransitionName(avatar, TRANSITION_SHARED_ELEMENT)

        title = presenter.displayName

        avatar.loadAvatar(presenter.photoLarge)

        about.text = "${presenter.description}\n${presenter.website}".trim()

        listAdapter = UniversalAdapter(
                { parent, _ -> RecordingHolder.inflate(parent) },
                { vh, _, item ->
                    vh.bind(item, RecordingType.PRESENTER, object : RecordingHolder.MoreOptions {
                        override fun play(item: Recording) {
                            NowPlayingActivity.launch(this@PresenterActivity, item)
                        }

                        override fun share(content: String) {
                            Helper.shareText(this@PresenterActivity, content)
                        }

                        override fun favorite(enabled: Boolean) {
                            //TODO: Implement
                        }

                    })
                }
        )

        listView.apply {
            vertical()
            adapter = listAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        if (listAdapter.itemCount > 0) {
            progressBar.hide()
        }
    }

    companion object {

        private const val TRANSITION_SHARED_ELEMENT = "image:shared_element_transition"

        fun view(context: Activity, presenter: Presenter, sharedElement: View) {
            ViewCompat.setTransitionName(sharedElement, TRANSITION_SHARED_ELEMENT)

            val intent = Intent(context, PresenterActivity::class.java)
            intent.putExtra(BundledExtras.PRESENTER, presenter)

            val options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(context, sharedElement, TRANSITION_SHARED_ELEMENT)

            ActivityCompat.startActivity(context, intent, options.toBundle())
        }
    }
}