package dev.olog.presentation.tab.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.tab.TabFragment

@Subcomponent(modules = arrayOf(
        TabFragmentModule::class
))
@PerFragment
interface TabFragmentSubComponent : AndroidInjector<TabFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TabFragment>()

}