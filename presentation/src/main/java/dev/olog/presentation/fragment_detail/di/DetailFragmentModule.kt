package dev.olog.presentation.fragment_detail.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentManager
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dagger.NestedFragmentManager
import dev.olog.presentation.fragment_detail.DetailFragment
import dev.olog.presentation.fragment_detail.DetailFragmentDataType
import dev.olog.presentation.fragment_detail.DetailFragmentViewModel
import dev.olog.presentation.fragment_detail.DetailFragmentViewModelFactory

@Module
class DetailFragmentModule(
        private val fragment: DetailFragment
) {

    @Provides
    fun provideInstance(): DetailFragment = fragment

    @Provides
    @FragmentLifecycle
    internal fun lifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    internal fun provideMediaId(): String {
        return fragment.arguments!!.getString(DetailFragment.ARGUMENTS_MEDIA_ID)
    }

    @Provides
    @NestedFragmentManager
    fun provideFragmentManager(): FragmentManager {
        return fragment.childFragmentManager
    }

    @Provides
    internal fun provideEnums() = DetailFragmentDataType.values()

    @Provides
    internal fun provideViewModel(factory: DetailFragmentViewModelFactory): DetailFragmentViewModel {
        return ViewModelProviders.of(fragment, factory)
                .get(DetailFragmentViewModel::class.java)
    }

}