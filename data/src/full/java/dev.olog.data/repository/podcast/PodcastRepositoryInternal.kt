package dev.olog.data.repository.podcast

import android.content.ContentUris
import android.content.Context
import dev.olog.core.entity.track.Song
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.di.qualifier.Podcast
import dev.olog.data.mapper.toSong
import dev.olog.data.queries.TrackQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.utils.queryAll
import dev.olog.data.utils.queryOne
import dev.olog.shared.ApplicationContext
import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class PodcastRepositoryInternal @Inject constructor(
    @ApplicationContext context: Context,
    private val schedulers: Schedulers,
    @Podcast private val queries: TrackQueries
) : BaseRepository<Song, Long>(context, schedulers) {

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(queries.tableUri, true)
    }

    override fun queryAll(): List<Song> {
//        DON'T ASSERT MAIN THREAD
        val cursor = queries.getAll()
        return contentResolver.queryAll(cursor) { it.toSong() }
    }

    override fun getByParam(param: Long): Song? {
        assertBackgroundThread()
        val cursor = queries.getByParam(param)
        return contentResolver.queryOne(cursor) { it.toSong() }
    }

    override fun observeByParam(param: Long): Flow<Song?> {
        val uri = ContentUris.withAppendedId(queries.tableUri, param)
        val contentUri = ContentUri(uri, true)
        return observeByParamInternal(contentUri) { getByParam(param) }
            .distinctUntilChanged()
            .flowOn(schedulers.cpu)

    }
}