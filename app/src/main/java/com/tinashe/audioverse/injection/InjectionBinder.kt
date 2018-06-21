package com.tinashe.audioverse.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tinashe.audioverse.ui.home.HomeActivity
import com.tinashe.audioverse.ui.home.HomeViewModel
import com.tinashe.audioverse.ui.home.tab.BaseTabFragment
import com.tinashe.audioverse.ui.home.tab.TabViewModel
import com.tinashe.audioverse.ui.presenter.PresenterActivity
import com.tinashe.audioverse.ui.presenter.PresenterViewModel
import com.tinashe.audioverse.ui.search.SearchActivity
import com.tinashe.audioverse.ui.search.SearchViewModel
import com.tinashe.audioverse.ui.splash.SplashActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class InjectionBinder {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun bindNavigationViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TabViewModel::class)
    internal abstract fun bindTabViewModel(tabViewModel: TabViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PresenterViewModel::class)
    internal abstract fun bindPresenterViewModel(presenterViewModel: PresenterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    internal abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFractory(factory: ViewModelFactory): ViewModelProvider.Factory


    @ContributesAndroidInjector
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector
    abstract fun bindTabFragment(): BaseTabFragment

    @ContributesAndroidInjector
    abstract fun bindPresenterActivity(): PresenterActivity

    @ContributesAndroidInjector
    abstract fun bindSearchActivity(): SearchActivity

}