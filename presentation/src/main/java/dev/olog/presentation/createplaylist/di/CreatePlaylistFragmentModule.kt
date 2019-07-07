package dev.olog.presentation.createplaylist.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.core.entity.PlaylistType
import dev.olog.presentation.createplaylist.CreatePlaylistFragment
import dev.olog.presentation.createplaylist.CreatePlaylistFragmentViewModel

@Module(includes = [CreatePlaylistFragmentModule.Binding::class])
class CreatePlaylistFragmentModule(private val fragment: CreatePlaylistFragment) {

    @Provides
    @FragmentLifecycle
    fun provideLifecycle() = fragment.lifecycle

    @Provides
    fun providePlaylistType(): PlaylistType {
        val type = fragment.arguments!!.getInt(CreatePlaylistFragment.ARGUMENT_PLAYLIST_TYPE)
        return PlaylistType.values()[type]
    }

    @Module
    interface Binding {

        @Binds
        @IntoMap
        @ViewModelKey(CreatePlaylistFragmentViewModel::class)
        fun provideViewModel(viewModel: CreatePlaylistFragmentViewModel): ViewModel

    }

}