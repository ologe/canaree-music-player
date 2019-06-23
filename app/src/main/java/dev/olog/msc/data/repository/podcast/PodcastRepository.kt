package dev.olog.msc.data.repository.podcast

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.podcast.Podcast
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.PodcastPositionEntity
import dev.olog.msc.data.mapper.toPodcast
import dev.olog.msc.data.mapper.toUneditedPodcast
import dev.olog.msc.domain.gateway.PodcastGateway
import dev.olog.msc.domain.gateway.UsedImageGateway
import dev.olog.shared.debounceFirst
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

private val PROJECTION = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.DATE_ADDED,
        "album_artist"
)

private const val SELECTION = "${MediaStore.Audio.Media.DURATION} > 20000 AND ${MediaStore.Audio.Media.IS_PODCAST} <> 0"

private const val SORT_ORDER = "lower(${MediaStore.Audio.Media.TITLE})"

class PodcastRepository @Inject constructor(
    appDatabase: AppDatabase,
    @ApplicationContext private val context: Context,
    private  val rxContentResolver: BriteContentResolver,
    private  val usedImageGateway: UsedImageGateway

): PodcastGateway {

    private val podcastPositionDao = appDatabase.podcastPositionDao()

    private fun queryAllData(): Observable<List<Podcast>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, PROJECTION, SELECTION,
                null, SORT_ORDER, true
        )
                .debounceFirst()
                .lift(SqlBrite.Query.mapToList { mapToPodcast(it) })
                .doOnError { it.printStackTrace() }
                .onErrorReturn { listOf() }
    }

    private fun mapToPodcast(cursor: Cursor): Podcast {
        return cursor.toPodcast()
    }

    private val cachedData = queryAllData()
            .replay(1)
            .refCount()

    override fun getAll(): Observable<List<Podcast>> = cachedData

    override fun getByParam(param: Long): Observable<Podcast> {
        return cachedData.map { list -> list.first { it.id == param } }
    }

    override fun getByAlbumId(albumId: Long): Observable<Podcast> {
        return cachedData.map { list -> list.first { it.albumId == albumId } }
    }

    override fun getUneditedByParam(podcastId: Long): Observable<Podcast> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, PROJECTION, "${MediaStore.Audio.Media._ID} = ?",
                arrayOf("$podcastId"), " ${MediaStore.Audio.Media._ID} ASC LIMIT 1", false
        )
                .debounceFirst()
                .lift(SqlBrite.Query.mapToOne {
                    it.toUneditedPodcast()
                }).distinctUntilChanged()
    }

    override fun getAllUnfiltered(): Observable<List<Podcast>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI,
                PROJECTION,
                SELECTION,
                null,
                SORT_ORDER,
                false
        )
                .debounceFirst()
                .lift(SqlBrite.Query.mapToList { it.toPodcast() })
                .doOnError { it.printStackTrace() }
                .onErrorReturnItem(listOf())
    }

    override fun deleteSingle(podcastId: Long): Completable {
        return Single.fromCallable {
            context.contentResolver.delete(MEDIA_STORE_URI, "${BaseColumns._ID} = ?", arrayOf("$podcastId"))
        }
                .filter { it > 0 }
                .flatMapSingle { getByParam(podcastId).firstOrError() }
                .map { File(it.path) }
                .filter { it.exists() }
                .map { it.delete() }
                .toSingle()
                .ignoreElement()

    }

    override fun deleteGroup(podcastList: List<Podcast>): Completable {
        return Flowable.fromIterable(podcastList)
                .map { it.id }
                .flatMapCompletable { deleteSingle(it).subscribeOn(Schedulers.io()) }
    }

    override fun getCurrentPosition(podcastId: Long, duration: Long): Long {
        val position = podcastPositionDao.getPosition(podcastId) ?: 0L
        if (position > duration - 1000 * 5){
            // if last 5 sec, restart
            return 0L
        }
        return position
    }

    override fun saveCurrentPosition(podcastId: Long, position: Long) {
        podcastPositionDao.setPosition(PodcastPositionEntity(podcastId, position))
    }
}