package dev.olog.presentation.fragment_detail.recently_added.di

import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.NestedFragmentLifecycle
import dev.olog.presentation.fragment_detail.recently_added.DetailRecentlyAddedFragment

@Module
class DetailRecentlyAddedFragmentModule(
        private val fragment: DetailRecentlyAddedFragment
) {

    @Provides
    @NestedFragmentLifecycle
    fun provideFragmentLifecycle() = fragment.lifecycle

}