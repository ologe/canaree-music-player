package dev.olog.presentation.recentlyadded.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.core.MediaId
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.presentation.recentlyadded.RecentlyAddedFragment
import dev.olog.presentation.recentlyadded.RecentlyAddedFragmentViewModel
import dev.olog.shared.android.extensions.getArgument

@Module
abstract class RecentlyAddedFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(RecentlyAddedFragmentViewModel::class)
    abstract fun provideViewModel(factory: RecentlyAddedFragmentViewModel): ViewModel

    companion object {

        @Provides
        internal fun provideMediaId(instance: RecentlyAddedFragment): MediaId {
            val mediaId = instance.getArgument<String>(RecentlyAddedFragment.ARGUMENTS_MEDIA_ID)
            return MediaId.fromString(mediaId)
        }

    }

}