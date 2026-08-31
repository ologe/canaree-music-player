package dev.olog.presentation.recentlyadded.di

import dagger.Module
import dagger.Provides
import dev.olog.core.MediaId
import dev.olog.presentation.recentlyadded.RecentlyAddedFragment
import dev.olog.shared.android.extensions.getArgument

@Module
abstract class RecentlyAddedFragmentModule {

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideMediaId(instance: RecentlyAddedFragment): MediaId {
            val mediaId = instance.getArgument<String>(RecentlyAddedFragment.ARGUMENTS_MEDIA_ID)
            return MediaId.fromString(mediaId)
        }

    }

}