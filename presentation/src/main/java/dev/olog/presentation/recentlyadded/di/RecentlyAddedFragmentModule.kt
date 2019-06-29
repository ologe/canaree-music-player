package dev.olog.presentation.recentlyadded.di

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.recentlyadded.RecentlyAddedFragment
import dev.olog.presentation.recentlyadded.RecentlyAddedFragmentViewModel
import dev.olog.core.MediaId

@Module(includes = [RecentlyAddedFragmentModule.Binding::class])
class RecentlyAddedFragmentModule(
        private val fragment: RecentlyAddedFragment
) {

    @Provides
    @FragmentLifecycle
    internal fun lifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    internal fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(RecentlyAddedFragment.ARGUMENTS_MEDIA_ID)!!
        return MediaId.fromString(mediaId)
    }

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(RecentlyAddedFragmentViewModel::class)
        fun provideViewModel(factory: RecentlyAddedFragmentViewModel): ViewModel

    }

}