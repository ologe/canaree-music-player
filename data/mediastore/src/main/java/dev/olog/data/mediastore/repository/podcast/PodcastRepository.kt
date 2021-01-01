package dev.olog.data.mediastore.repository.podcast

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.base.Id
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.prefs.SortPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.data.local.podcast.PodcastPositionDao
import dev.olog.data.local.podcast.PodcastPositionEntity
import dev.olog.data.mediastore.mapper.toTrack
import dev.olog.data.mediastore.queries.TrackQueries
import dev.olog.data.mediastore.repository.BaseRepository
import dev.olog.data.mediastore.repository.ContentUri
import dev.olog.data.mediastore.utils.queryAll
import dev.olog.data.mediastore.utils.queryOne
import dev.olog.shared.value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.io.File
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.milliseconds
import kotlin.time.seconds

internal class PodcastRepository @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferencesGateway,
    blacklistPrefs: BlacklistPreferences,
    private val podcastPositionDao: PodcastPositionDao,
    schedulers: Schedulers
) : BaseRepository<Track, Id>(context, schedulers), PodcastGateway {

    private val queries = TrackQueries(
        schedulers = schedulers,
        contentResolver = context.contentResolver,
        blacklistPrefs = blacklistPrefs,
        sortPrefs = sortPrefs,
        isPodcast = true
    )

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    override suspend fun queryAll(): List<Track> {
        val cursor = queries.getAll()
        return contentResolver.queryAll(cursor, Cursor::toTrack)
    }

    override suspend fun getByParam(param: Id): Track? {
        return publisher.value?.find { it.id == param }
            ?: contentResolver.queryOne(queries.getByParam(param), Cursor::toTrack)
    }

    override fun observeByParam(param: Id): Flow<Track?> {
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, param)
        val contentUri = ContentUri(uri, true)
        return observeByParamInternal(contentUri) { getByParam(param) }
            .distinctUntilChanged()
    }

    override suspend fun deleteSingle(id: Id) {
        return deleteInternal(id)
    }

    override suspend fun deleteGroup(podcastList: List<Track>) {
        for (id in podcastList) {
            deleteInternal(id.id)
        }
    }

    private suspend fun deleteInternal(id: Id) {
        val path = getByParam(id)!!.path
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
        val deleted = contentResolver.delete(uri, null, null)

        if (deleted < 1) {
            Log.w("PodcastRepo", "podcast not found $id")
            return
        }
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    override suspend fun getCurrentPosition(podcastId: Long, duration: Duration): Duration {
        val position = podcastPositionDao.getPosition(podcastId)?.milliseconds ?: 0.milliseconds
        if (position > duration - 5.seconds) {
            // if last 5 sec, restart
            return 0.milliseconds
        }
        return position
    }

    override suspend fun saveCurrentPosition(podcastId: Long, position: Duration) {
        val entity = PodcastPositionEntity(
            id = podcastId,
            position = position.toLongMilliseconds()
        )
        podcastPositionDao.setPosition(entity)
    }

    override suspend fun getByAlbumId(albumId: Id): Track? {
        return publisher.value?.find { it.albumId == albumId }
            ?: contentResolver.queryOne(queries.getByAlbumId(albumId), Cursor::toTrack)
    }
}