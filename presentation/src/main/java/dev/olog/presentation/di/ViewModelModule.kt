package dev.olog.presentation.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.presentation.navigator.Navigator

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun provideMediaId(handle: SavedStateHandle): MediaId {
        val mediaId = handle.get<String>(Navigator.MEDIA_ID_ARG)!!
        return MediaId.fromString(mediaId)
    }

    @Provides
    fun providePlaylistType(handle: SavedStateHandle): PlaylistType {
        val type = handle.get<Int>(Navigator.PLAYLIST_TYPE_ARG)!!
        return PlaylistType.values()[type]
    }

}