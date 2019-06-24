package dev.olog.data.repository.podcast

import android.content.ContentUris
import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.Id
import dev.olog.core.gateway.PodcastGateway2
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.PodcastPositionEntity
import dev.olog.data.mapper.toSong
import dev.olog.data.queries.TrackQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.utils.queryAll
import dev.olog.data.utils.queryOne
import dev.olog.shared.assertBackground
import dev.olog.shared.assertBackgroundThread
import io.reactivex.Completable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.io.File
import javax.inject.Inject

internal class PodcastRepository2 @Inject constructor(
    @ApplicationContext context: Context,
    appDatabase: AppDatabase,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences
) : BaseRepository<Song, Id>(context), PodcastGateway2 {

    private val podcastPositionDao = appDatabase.podcastPositionDao()

    private val queries = TrackQueries(
        context.contentResolver, blacklistPrefs,
        sortPrefs, true
    )

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    override fun queryAll(): List<Song> {
        assertBackgroundThread()
        val cursor = queries.getAll()
        return context.contentResolver.queryAll(cursor) { it.toSong() }
    }

    override fun getByParam(param: Id): Song? {
        assertBackgroundThread()
        val cursor = queries.getByParam(param)
        return context.contentResolver.queryOne(cursor) { it.toSong() }
    }

    override fun observeByParam(param: Id): Flow<Song?> {
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, param)
        val contentUri = ContentUri(uri, true)
        return observeByParamInternal(contentUri) { getByParam(param) }
            .distinctUntilChanged()
            .assertBackground()
    }

    override fun deleteSingle(id: Id): Completable {
        return Completable.fromCallable { deleteInternal(id) }
    }

    override fun deleteGroup(ids: List<Song>): Completable {
        return Completable.fromCallable {
            for (id in ids) {
                deleteInternal(id.id)
            }
        }
    }

    private fun deleteInternal(id: Id) {
        assertBackgroundThread()
        val deleted = context.contentResolver.delete(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            "${BaseColumns._ID} = ?",
            arrayOf("$id")
        )
        if (deleted < 1) {
            Log.w("SongRepo", "song not found $id")
            return
        }
        val path = getByParam(id)!!.path
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
        podcastPositionDao.setPosition(PodcastPositionEntity(podcastId, position))
    }
}