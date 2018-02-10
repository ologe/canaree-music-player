package dev.olog.msc.presentation.detail.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.RecyclerView
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.dagger.PerFragment
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.detail.DetailFragmentDataType
import dev.olog.msc.presentation.detail.DetailFragmentViewModel
import dev.olog.msc.presentation.detail.DetailFragmentViewModelFactory
import dev.olog.msc.utils.MediaId

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
    internal fun provideMediaId(): MediaId {
        val mediaId = fragment.arguments!!.getString(DetailFragment.ARGUMENTS_MEDIA_ID)
        return MediaId.fromString(mediaId)
    }

    @Provides
    @PerFragment
    fun provideRecycledViewPool() = RecyclerView.RecycledViewPool()

    @Provides
    internal fun provideEnums() = DetailFragmentDataType.values()

    @Provides
    internal fun provideViewModel(factory: DetailFragmentViewModelFactory): DetailFragmentViewModel {
        return ViewModelProviders.of(fragment, factory)
                .get(DetailFragmentViewModel::class.java)
    }

}