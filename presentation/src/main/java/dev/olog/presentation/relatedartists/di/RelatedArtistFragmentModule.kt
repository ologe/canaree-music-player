package dev.olog.presentation.relatedartists.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.core.MediaId
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.presentation.relatedartists.RelatedArtistFragment
import dev.olog.presentation.relatedartists.RelatedArtistFragmentViewModel
import dev.olog.shared.android.extensions.getArgument

@Module
abstract class RelatedArtistFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(RelatedArtistFragmentViewModel::class)
    abstract fun provideViewModel(factory: RelatedArtistFragmentViewModel): ViewModel

    companion object {

        @Provides
        internal fun provideMediaId(instance: RelatedArtistFragment): MediaId {
            val mediaId = instance.getArgument<String>(RelatedArtistFragment.ARGUMENTS_MEDIA_ID)
            return MediaId.fromString(mediaId)
        }
    }

}