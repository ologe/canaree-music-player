package dev.olog.msc.presentation.related.artists.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.presentation.related.artists.RelatedArtistFragment
import dev.olog.msc.presentation.related.artists.RelatedArtistFragmentViewModelFactory
import dev.olog.msc.utils.MediaId
import dev.olog.presentation.fragment_related_artist.RelatedArtistViewModel

@Module
class RelatedArtistFragmentModule(
        private val fragment: RelatedArtistFragment
) {

    @Provides
    @FragmentLifecycle
    internal fun lifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    internal fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(RelatedArtistFragment.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    internal fun provideViewModel(factory: RelatedArtistFragmentViewModelFactory): RelatedArtistViewModel {

        return ViewModelProviders.of(fragment, factory).get(RelatedArtistViewModel::class.java)
    }

}