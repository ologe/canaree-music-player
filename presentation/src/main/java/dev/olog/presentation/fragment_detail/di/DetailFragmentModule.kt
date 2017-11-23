package dev.olog.presentation.fragment_detail.di

import android.arch.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_detail.DetailFragment

@Module
class DetailFragmentModule(
        private val fragment: DetailFragment
) {

    @Provides
    fun provideInstance(): DetailFragment = fragment

    @Provides
    @FragmentLifecycle
    internal fun lifecycle(): Lifecycle {
        return fragment.lifecycle
    }

    @Provides
    internal fun provideMediaId(): String {
        return fragment.arguments!!.getString(DetailFragment.ARGUMENTS_MEDIA_ID)
    }

}