package dev.olog.presentation.fragment_tab.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_tab.TabFragment
import dev.olog.presentation.fragment_tab.TabFragmentViewModel
import dev.olog.presentation.fragment_tab.TabFragmentViewModelFactory
import dev.olog.shared.MediaIdCategory

@Module
class TabFragmentModule(
        private val fragment: TabFragment
) {

    @Provides
    internal fun provideSource(): MediaIdCategory {
        val ordinalCategory = fragment.arguments!!.getInt(TabFragment.ARGUMENTS_SOURCE)
        return MediaIdCategory.values()[ordinalCategory]
    }

    @Provides
    @FragmentLifecycle
    internal fun provideLifecycle(): Lifecycle = fragment.lifecycle

    // using 'FragmentActivity' scope to share this viewModel through all
    // tab fragments
    @Provides
    internal fun viewModel(activity: FragmentActivity, factory: TabFragmentViewModelFactory): TabFragmentViewModel {
        return ViewModelProviders.of(activity, factory).get(TabFragmentViewModel::class.java)
    }


}