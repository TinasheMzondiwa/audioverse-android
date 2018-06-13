package com.tinashe.audioverse.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.tinashe.audioverse.R
import com.tinashe.audioverse.ui.home.tab.BaseTabFragment
import com.tinashe.audioverse.utils.hide
import kotlinx.android.synthetic.main.fragment_tabbed.*

class BaseNavigationFragment : Fragment() {

    private var navigation: Int = Navigation.PRESENTATIONS

    private lateinit var tabsAdapter: TabsListAdapter

    companion object {
        fun newInstance(@Navigation item: Int): BaseNavigationFragment {
            val fragment = BaseNavigationFragment()
            fragment.navigation = item
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tabbed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { bar, offset ->
            bar.isActivated = offset < 0
        })

        inflateMenu()

        val title = when (navigation) {
            Navigation.PRESENTATIONS -> R.string.presentations
            Navigation.PRESENTERS -> R.string.presenters
            Navigation.LISTS -> R.string.my_lists
            else -> R.string.blank
        }

        toolbar.title = getString(title)

        tabsAdapter = TabsListAdapter(childFragmentManager, navigation)
        viewPager.adapter = tabsAdapter

        if (navigation == Navigation.PRESENTERS) {
            tabLayout.hide()
        } else {
            tabLayout.setupWithViewPager(viewPager)
        }
    }

    private fun inflateMenu() {
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener {

            return@setOnMenuItemClickListener true
        }
    }

    fun scrollToTop() {
        val fragment = tabsAdapter.getItem(viewPager.currentItem)
        if (fragment is BaseTabFragment) {
            fragment.scrollToTop()
        }
    }
}