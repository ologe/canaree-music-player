package dev.olog.data.repository

import android.content.ContentResolver
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns.*
import android.provider.MediaStore.Audio.Media.DURATION
import android.provider.MediaStore.Audio.Media.TITLE
import com.squareup.sqlbrite2.BriteContentResolver
import dev.olog.data.mapper.toSong
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.SongGateway
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepository @Inject constructor(
        private val contentResolver: ContentResolver,
        rxContentResolver: BriteContentResolver

) : SongGateway {

    companion object {

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
                MediaStore.Audio.Media.DATE_ADDED
        )

        private const val SELECTION = "$IS_MUSIC <> 0 AND $IS_ALARM = 0 AND $IS_PODCAST = 0 " +
                "AND $TITLE NOT LIKE ? AND $DURATION > ?"

        private val SELECTION_ARGS = arrayOf("AUD%", "20000")

        private val SORT_ORDER = "lower(${MediaStore.Audio.Media.TITLE})"
    }

    private val contentProviderObserver = rxContentResolver
            .createQuery(
                    MEDIA_STORE_URI,
                    PROJECTION,
                    SELECTION,
                    SELECTION_ARGS,
                    SORT_ORDER,
                    false
            ).mapToList { it.toSong() }
            .toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()
            .replay(1)
            .refCount()

    override fun getAll(): Flowable<List<Song>> = contentProviderObserver

    override fun getByParam(param: Long): Flowable<Song> {
        return getAll().flatMapSingle { it.toFlowable()
                .filter { it.id == param }
                .firstOrError()
        }
    }

    override fun deleteSingle(songId: Long): Completable {

        return Single.fromCallable { contentResolver.delete(MEDIA_STORE_URI,
                "${BaseColumns._ID} = ?",
                arrayOf("$songId"))
        }.filter { it > 0 }
                .flatMapSingle { getByParam(songId).firstOrError() }
                .map { File(it.path) }
                .filter { it.exists() }
                .map { it.delete() }
                .toSingle()
                .toCompletable()

    }

    override fun deleteGroup(songList: List<Song>): Completable {
        return Flowable.fromIterable(songList)
                .map { it.id }
                .flatMapCompletable { deleteSingle(it).subscribeOn(Schedulers.io()) }
    }
}
