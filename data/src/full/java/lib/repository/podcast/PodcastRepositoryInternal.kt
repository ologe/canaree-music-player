package dev.olog.lib.repository.podcast

import android.content.ContentUris
import android.content.Context
import dev.olog.domain.entity.track.Song
import dev.olog.domain.schedulers.Schedulers
import dev.olog.lib.di.qualifier.Podcast
import dev.olog.lib.mapper.toSong
import dev.olog.lib.queries.TrackQueries
import dev.olog.lib.repository.BaseRepository
import dev.olog.lib.repository.ContentUri
import dev.olog.lib.utils.queryAll
import dev.olog.lib.utils.queryOne
import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class PodcastRepositoryInternal @Inject constructor(
    context: Context,
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