package com.tinashe.audioverse.ui.home

import android.os.Bundle
import androidx.lifecycle.Observer
import com.tinashe.audioverse.R
import com.tinashe.audioverse.injection.ViewModelFactory
import com.tinashe.audioverse.ui.base.BaseActivity
import com.tinashe.audioverse.utils.getViewModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

class HomeActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: HomeViewModel

    private var currFragment: BaseNavigationFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        AndroidInjection.inject(this)

        viewModel = getViewModel(this, viewModelFactory)
        viewModel.navigationHolder.observe(this, Observer { navigation ->
            navigation?.let {

                currFragment = BaseNavigationFragment.newInstance(it)
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, currFragment!!)
                        .commit()
            }
        })

        navigation.setOnNavigationItemSelectedListener {

            if (it.isChecked) {
                currFragment?.scrollToTop()

                return@setOnNavigationItemSelectedListener false
            }

            viewModel.switchNavigation(it.itemId)

            return@setOnNavigationItemSelectedListener true
        }

        viewModel.navigationHolder.value?.let {
            navigation.selectedItemId = it
        }
    }
}