package dev.olog.presentation.fragment_tab.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_tab.TabFragment

@Module
class TabFragmentModule(
        private val fragment: TabFragment
) {

    @Provides
    internal fun provideSource(): Int {
        return fragment.arguments!!.getInt(TabFragment.ARGUMENTS_SOURCE)
    }

    @Provides
    @FragmentLifecycle
    internal fun provideLifecycle(): Lifecycle {
        return fragment.lifecycle
    }

}