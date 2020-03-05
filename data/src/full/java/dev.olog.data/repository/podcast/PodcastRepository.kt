package dev.olog.data.repository.podcast

import android.content.ContentUris
import android.content.Context
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.db.PodcastPositionDao
import dev.olog.data.di.qualifier.Podcast
import dev.olog.data.mapper.toSong
import dev.olog.data.model.db.PodcastPositionEntity
import dev.olog.data.queries.TrackQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.utils.assertBackground
import dev.olog.data.utils.assertBackgroundThread
import dev.olog.data.utils.queryAll
import dev.olog.data.utils.queryOne
import dev.olog.shared.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber
import java.io.File
import javax.inject.Inject

internal class PodcastRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val podcastPositionDao: PodcastPositionDao,
    schedulers: Schedulers,
    @Podcast private val queries: TrackQueries
) : BaseRepository<Song, Id>(context, schedulers), PodcastGateway {

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(queries.tableUri, true)
    }

    override fun queryAll(): List<Song> {
//        assertBackgroundThread()
        val cursor = queries.getAll()
        return contentResolver.queryAll(cursor) { it.toSong() }
    }

    override fun getByParam(param: Id): Song? {
        assertBackgroundThread()
        val cursor = queries.getByParam(param)
        return contentResolver.queryOne(cursor) { it.toSong() }
    }

    override fun observeByParam(param: Id): Flow<Song?> {
        val uri = ContentUris.withAppendedId(queries.tableUri, param)
        val contentUri = ContentUri(uri, true)
        return observeByParamInternal(contentUri) { getByParam(param) }
            .distinctUntilChanged()
            .assertBackground()
    }

    override suspend fun deleteSingle(id: Id) {
        return deleteInternal(id)
    }

    override suspend fun deleteGroup(podcastList: List<Id>) {
        for (id in podcastList) {
            deleteInternal(id)
        }
    }

    private fun deleteInternal(id: Id) {
        assertBackgroundThread()
        val path = getByParam(id)!!.path
        val uri = ContentUris.withAppendedId(queries.tableUri, id)
        val deleted = contentResolver.delete(uri, null, null)

        if (deleted < 1) {
            Timber.w("PodcastRepo: podcast not found $id")
            return
        }
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    override fun getCurrentPosition(podcastId: Long, duration: Long): Long {
        assertBackgroundThread()
        val position = podcastPositionDao.getPosition(podcastId) ?: 0L
        if (position > duration - 1000 * 5) {
            // if last 5 sec, restart
            return 0L
        }
        return position
    }

    override fun saveCurrentPosition(podcastId: Long, position: Long) {
        assertBackgroundThread()
        podcastPositionDao.setPosition(
            PodcastPositionEntity(
                podcastId,
                position
            )
        )
    }

    override fun getByAlbumId(albumId: Id): Song? {
        assertBackgroundThread()
        val item = channel.valueOrNull?.find { it.albumId == albumId }
        if (item != null){
            return item
        }

        val cursor = queries.getByAlbumId(albumId)
        return contentResolver.queryOne(cursor) { it.toSong() }
    }
}