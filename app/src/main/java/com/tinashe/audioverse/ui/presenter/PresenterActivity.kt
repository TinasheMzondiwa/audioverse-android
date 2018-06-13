package com.tinashe.audioverse.ui.presenter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import com.squareup.picasso.Picasso
import com.tinashe.audioverse.R
import com.tinashe.audioverse.data.model.BundledExtras
import com.tinashe.audioverse.data.model.Presenter
import com.tinashe.audioverse.data.model.Recording
import com.tinashe.audioverse.data.model.RecordingType
import com.tinashe.audioverse.injection.ViewModelFactory
import com.tinashe.audioverse.ui.home.tab.vh.RecordingHolder
import com.tinashe.audioverse.utils.custom.PicassoCircleTransform
import com.tinashe.audioverse.utils.custom.UniversalAdapter
import com.tinashe.audioverse.utils.getViewModel
import com.tinashe.audioverse.utils.hide
import com.tinashe.audioverse.utils.show
import com.tinashe.audioverse.utils.vertical
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_presenter.*
import timber.log.Timber
import javax.inject.Inject

class PresenterActivity : AppCompatActivity() {

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
        viewModel.presentations.observe(this, Observer {

            it?.let {
                if (it.isNotEmpty()) {
                    presentationsLabel.show()
                    progressBar.hide()
                }

                listAdapter.items = it.toMutableList()
            } ?: progressBar.hide()
        })
        viewModel.fetchPresentations(presenter.id)

    }

    private fun initPresenter(presenter: Presenter) {
        title = presenter.displayName

        Picasso.get()
                .load(presenter.photoLarge)
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .transform(PicassoCircleTransform())
                .into(avatar)

        about.text = presenter.description

        listAdapter = UniversalAdapter(
                { parent, _ -> RecordingHolder.inflate(parent) },
                { vh, _, item -> vh.bind(item, RecordingType.PRESENTER) }
        )

        listView.apply {
            vertical()
            adapter = listAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun view(context: Context, presenter: Presenter) {
            val intent = Intent(context, PresenterActivity::class.java)
            intent.putExtra(BundledExtras.PRESENTER, presenter)
            context.startActivity(intent)
        }
    }
}