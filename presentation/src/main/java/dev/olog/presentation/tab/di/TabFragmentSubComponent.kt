package dev.olog.presentation.tab.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.feature.presentation.base.dagger.ScreenScope
import dev.olog.presentation.tab.TabFragment

@Subcomponent(modules = [TabFragmentModule::class])
@ScreenScope
interface TabFragmentSubComponent : AndroidInjector<TabFragment> {

    @Subcomponent.Factory
    interface Builder : AndroidInjector.Factory<TabFragment>

}