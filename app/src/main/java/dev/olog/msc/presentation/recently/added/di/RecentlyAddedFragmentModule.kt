package dev.olog.msc.presentation.recently.added.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.ViewModelKey
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragment
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragmentViewModel
import dev.olog.msc.utils.MediaId

@Module(includes = [RecentlyAddedFragmentModule.Binding::class])
class RecentlyAddedFragmentModule(
        private val fragment: RecentlyAddedFragment
) {

    @Provides
    @FragmentLifecycle
    internal fun lifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    internal fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(RecentlyAddedFragment.ARGUMENTS_MEDIA_ID)
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