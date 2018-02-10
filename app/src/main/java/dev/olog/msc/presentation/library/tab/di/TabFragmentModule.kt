package dev.olog.msc.presentation.library.tab.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.presentation.library.tab.TabFragment
import dev.olog.msc.presentation.library.tab.TabFragmentViewModel
import dev.olog.msc.presentation.library.tab.TabFragmentViewModelFactory
import dev.olog.msc.utils.MediaIdCategory

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