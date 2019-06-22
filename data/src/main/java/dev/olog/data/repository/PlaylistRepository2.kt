package dev.olog.data.repository

import android.content.Context
import android.provider.MediaStore
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.Playlist
import dev.olog.core.entity.Song
import dev.olog.core.gateway.Id
import dev.olog.core.gateway.PlaylistGateway2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class PlaylistRepository2 @Inject constructor(
    @ApplicationContext context: Context
) : BaseRepository<Playlist, Id>(context), PlaylistGateway2 {

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, true)
    }

    override fun queryAll(): List<Playlist> {
        return listOf()
    }

    override fun getByParam(param: Id): Playlist? {
        return null
    }

    override fun observeByParam(param: Id): Flow<Playlist?> {
        return flow {  }
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        return listOf()
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        return flow {  }
    }
}