package dev.olog.presentation.fragment_detail.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.RecyclerView
import dagger.Module
import dagger.Provides
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_detail.*

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
    internal fun providePosition(): Int {
        return fragment.arguments!!.getInt(DetailFragment.ARGUMENTS_LIST_POSITION)
    }


    @Provides
    internal fun provideView(): DetailFragmentView = fragment

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