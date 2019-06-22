package dev.olog.data.repository

import android.content.Context
import android.provider.MediaStore
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.Genre
import dev.olog.core.entity.Song
import dev.olog.core.gateway.GenreGateway2
import dev.olog.core.gateway.Id
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class GenreRepository2 @Inject constructor(
    @ApplicationContext context: Context
) : BaseRepository<Genre, Id>(context), GenreGateway2 {

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, true)
    }

    override fun queryAll(): List<Genre> {
        return listOf()
    }

    override fun getByParam(param: Id): Genre? {
        return null
    }

    override fun observeByParam(param: Id): Flow<Genre?> {
        return flow {  }
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        return listOf()
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        return flow {  }
    }
}