package dev.olog.presentation.recentlyadded.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.feature.presentation.base.dagger.PerFragment
import dev.olog.presentation.recentlyadded.RecentlyAddedFragment

@Subcomponent(
    modules = [RecentlyAddedFragmentModule::class]
)
@PerFragment
interface RecentlyAddedFragmentSubComponent : AndroidInjector<RecentlyAddedFragment> {

    @Subcomponent.Factory
    interface Builder : AndroidInjector.Factory<RecentlyAddedFragment>

}