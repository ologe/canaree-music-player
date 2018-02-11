package dev.olog.msc.presentation.recently.added.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragment
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragmentViewModel
import dev.olog.msc.presentation.recently.added.RecentlyAddedFragmentViewModelFactory
import dev.olog.msc.utils.MediaId

@Module
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

    @Provides
    internal fun provideViewModel(factory: RecentlyAddedFragmentViewModelFactory): RecentlyAddedFragmentViewModel {

        return ViewModelProviders.of(fragment, factory).get(RecentlyAddedFragmentViewModel::class.java)
    }

}