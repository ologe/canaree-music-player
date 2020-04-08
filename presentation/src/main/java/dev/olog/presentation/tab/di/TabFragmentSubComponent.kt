package dev.olog.presentation.tab.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.feature.presentation.base.dagger.PerFragment
import dev.olog.presentation.tab.TabFragment

@Subcomponent(modules = [TabFragmentModule::class])
@PerFragment
interface TabFragmentSubComponent : AndroidInjector<TabFragment> {

    @Subcomponent.Factory
    interface Builder : AndroidInjector.Factory<TabFragment>

}