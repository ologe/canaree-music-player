package dev.olog.presentation.detail.di

import dagger.Module
import dagger.Provides
import dev.olog.core.MediaId
import dev.olog.presentation.detail.DetailFragment
import dev.olog.shared.android.extensions.getArgument

@Module
internal abstract class DetailFragmentModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        internal fun provideMediaId(instance: DetailFragment): MediaId {
            val mediaId = instance.getArgument<String>(DetailFragment.ARGUMENTS_MEDIA_ID)
            return MediaId.fromString(mediaId)
        }

    }


}