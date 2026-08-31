package dev.olog.presentation.relatedartists.di

import dagger.Module
import dagger.Provides
import dev.olog.core.MediaId
import dev.olog.presentation.relatedartists.RelatedArtistFragment
import dev.olog.shared.android.extensions.getArgument

@Module
abstract class RelatedArtistFragmentModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        internal fun provideMediaId(instance: RelatedArtistFragment): MediaId {
            val mediaId = instance.getArgument<String>(RelatedArtistFragment.ARGUMENTS_MEDIA_ID)
            return MediaId.fromString(mediaId)
        }
    }

}