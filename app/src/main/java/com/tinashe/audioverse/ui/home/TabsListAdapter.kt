package com.tinashe.audioverse.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.tinashe.audioverse.data.model.Section
import com.tinashe.audioverse.ui.home.tab.BaseTabFragment

class TabsListAdapter constructor(fm: FragmentManager, @Navigation navigation: Int) : FragmentStatePagerAdapter(fm) {

    private var fragments: List<BaseTabFragment>

    init {
        val container = mutableListOf<BaseTabFragment>()

        when(navigation){
            Navigation.LISTS -> {
                container.add(BaseTabFragment.newInstance("Favorites", Section.MY_LIST_FAVORITES))
                container.add(BaseTabFragment.newInstance("PlayLists", Section.MY_LIST_PLAY_LISTS))
                container.add(BaseTabFragment.newInstance("History", Section.MY_LIST_HISTORY))
            }
            Navigation.PRESENTATIONS -> {
                container.add(BaseTabFragment.newInstance("New", Section.RECORDINGS_NEW))
                container.add(BaseTabFragment.newInstance("Trending", Section.RECORDINGS_TRENDING))
                container.add(BaseTabFragment.newInstance("Featured", Section.RECORDINGS_FEATURED))
            }
            Navigation.PRESENTERS -> {
                container.add(BaseTabFragment.newInstance("Presenters", Section.PRESENTERS))
            }
        }

        fragments = container
    }

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? = fragments[position].title
}