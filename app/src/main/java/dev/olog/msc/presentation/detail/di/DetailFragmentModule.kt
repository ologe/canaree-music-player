package dev.olog.msc.presentation.detail.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModel
import android.support.v7.widget.RecyclerView
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.dagger.ViewModelKey
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.presentation.detail.DetailFragment
import dev.olog.msc.presentation.detail.DetailFragmentViewModel
import dev.olog.msc.utils.MediaId

@Module(includes = [DetailFragmentModule.Binding::class])
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

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(DetailFragmentViewModel::class)
        fun provideViewModel(viewModel: DetailFragmentViewModel): ViewModel

    }

}