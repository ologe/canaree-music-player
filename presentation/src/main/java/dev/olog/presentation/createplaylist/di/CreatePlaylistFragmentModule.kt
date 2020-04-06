package dev.olog.presentation.createplaylist.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.domain.entity.PlaylistType
import dev.olog.presentation.createplaylist.CreatePlaylistFragment
import dev.olog.presentation.createplaylist.CreatePlaylistFragmentViewModel
import dev.olog.presentation.dagger.ViewModelKey
import dev.olog.shared.android.extensions.getArgument

@Module
abstract class CreatePlaylistFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(CreatePlaylistFragmentViewModel::class)
    abstract fun provideViewModel(viewModel: CreatePlaylistFragmentViewModel): ViewModel

    companion object {

        @Provides
        fun providePlaylistType(instance: CreatePlaylistFragment): PlaylistType {
            val type = instance.getArgument<Int>(CreatePlaylistFragment.ARGUMENT_PLAYLIST_TYPE)
            return PlaylistType.values()[type]
        }
    }

}