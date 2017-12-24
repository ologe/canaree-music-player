package dev.olog.presentation.fragment_detail.most_player.di

import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.NestedFragmentLifecycle
import dev.olog.presentation.fragment_detail.most_player.DetailMostPlayedFragment

@Module
class DetailMostPlayedFragmentModule(
        private val fragment: DetailMostPlayedFragment
) {

    @Provides
    @NestedFragmentLifecycle
    fun provideFragmentLifecycle() = fragment.lifecycle

}