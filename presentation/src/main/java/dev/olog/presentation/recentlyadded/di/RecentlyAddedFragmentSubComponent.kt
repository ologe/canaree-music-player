package dev.olog.presentation.recentlyadded.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.feature.presentation.base.dagger.ScreenScope
import dev.olog.presentation.recentlyadded.RecentlyAddedFragment

@Subcomponent(
    modules = [RecentlyAddedFragmentModule::class]
)
@ScreenScope
interface RecentlyAddedFragmentSubComponent : AndroidInjector<RecentlyAddedFragment> {

    @Subcomponent.Factory
    interface Builder : AndroidInjector.Factory<RecentlyAddedFragment>

}