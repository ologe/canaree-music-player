package dev.olog.presentation.createplaylist.di

import dagger.Module
import dagger.Provides
import dev.olog.core.entity.PlaylistType
import dev.olog.presentation.createplaylist.CreatePlaylistFragment
import dev.olog.shared.android.extensions.getArgument

@Module
abstract class CreatePlaylistFragmentModule {

    @Module
    companion object {

        @Provides
        @JvmStatic
        fun providePlaylistType(instance: CreatePlaylistFragment): PlaylistType {
            val type = instance.getArgument<Int>(CreatePlaylistFragment.ARGUMENT_PLAYLIST_TYPE)
            return PlaylistType.values()[type]
        }
    }

}