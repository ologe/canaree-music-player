package dev.olog.presentation.fragment_related_artist.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_related_artist.RelatedArtistFragment
import dev.olog.presentation.fragment_related_artist.RelatedArtistFragmentViewModelFactory
import dev.olog.presentation.fragment_related_artist.RelatedArtistViewModel

@Module
class RelatedArtistFragmentModule(
        private val fragment: RelatedArtistFragment
) {

    @Provides
    @FragmentLifecycle
    internal fun lifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    internal fun provideMediaId(): String {
        return fragment.arguments!!.getString(RelatedArtistFragment.ARGUMENTS_MEDIA_ID)
    }

    @Provides
    internal fun provideViewModel(factory: RelatedArtistFragmentViewModelFactory): RelatedArtistViewModel {

        return ViewModelProviders.of(fragment, factory).get(RelatedArtistViewModel::class.java)
    }

}