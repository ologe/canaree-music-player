package dev.olog.msc.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.presentation.NavigationUtils
import dev.olog.presentation.createplaylist.CreatePlaylistFragment

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    internal fun provideMediaId(handle: SavedStateHandle): MediaId {
        return MediaId.fromString(handle[NavigationUtils.ARGUMENTS_MEDIA_ID]!!)
    }

    @Provides
    fun providePlaylistType(handle: SavedStateHandle): PlaylistType {
        return handle[NavigationUtils.ARGUMENTS_PLAYLIST_TYPE]!!
    }

}