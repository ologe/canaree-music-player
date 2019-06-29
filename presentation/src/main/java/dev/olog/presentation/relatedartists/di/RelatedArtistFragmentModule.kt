package dev.olog.presentation.relatedartists.di

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.relatedartists.RelatedArtistFragment
import dev.olog.presentation.relatedartists.RelatedArtistFragmentViewModel
import dev.olog.core.MediaId

@Module(includes = [RelatedArtistFragmentModule.Binding::class])
class RelatedArtistFragmentModule(
        private val fragment: RelatedArtistFragment
) {

    @Provides
    @FragmentLifecycle
    internal fun lifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    internal fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(RelatedArtistFragment.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(RelatedArtistFragmentViewModel::class)
        fun provideViewModel(factory: RelatedArtistFragmentViewModel): ViewModel

    }

}