package dev.olog.msc.presentation.library.tab.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.library.tab.TabFragment

@Subcomponent(modules = arrayOf(
        TabFragmentModule::class,
        TabFragmentViewModelModule::class,
        TabFragmentPodcastModule::class
))
@PerFragment
interface TabFragmentSubComponent : AndroidInjector<TabFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TabFragment>()

}