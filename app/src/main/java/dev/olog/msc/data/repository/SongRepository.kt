package dev.olog.msc.data.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.DURATION
import com.squareup.sqlbrite3.BriteContentResolver
import com.squareup.sqlbrite3.SqlBrite
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.Song
import dev.olog.msc.data.mapper.toSong
import dev.olog.msc.data.mapper.toUneditedSong
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.utils.getLong
import dev.olog.msc.utils.getString
import dev.olog.msc.utils.k.extension.debounceFirst
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
    MediaStore.Audio.Media.IS_PODCAST,
    "album_artist"
)

private const val SELECTION = "$DURATION > 20000 AND ${MediaStore.Audio.Media.IS_PODCAST} = 0"

private const val SORT_ORDER = "lower(${MediaStore.Audio.Media.TITLE})"

class SongRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val rxContentResolver: BriteContentResolver,
    private val appPrefsUseCase: AppPreferencesGateway

) : SongGateway {

    private fun queryAllData(): Observable<List<Song>> {
        return rxContentResolver.createQuery(
            MEDIA_STORE_URI, PROJECTION, SELECTION,
            null, SORT_ORDER, true
        )
            .debounceFirst()
            .lift(SqlBrite.Query.mapToList { mapToSong(it) })
            .doOnError { it.printStackTrace() }
            .onErrorReturn { listOf() }
    }

    private fun mapToSong(cursor: Cursor): Song {
        return cursor.toSong()
    }

    private val cachedData = queryAllData()
        .replay(1)
        .refCount()

    override fun getAll(): Observable<List<Song>> = cachedData

    override fun getByParam(param: Long): Observable<Song> {
        return cachedData.map { list -> list.first { it.id == param } }
    }

    override fun getByAlbumId(albumId: Long): Observable<Song> {
        return cachedData.map { list -> list.first { it.albumId == albumId } }
    }

    @SuppressLint("Recycle")
    override fun getByUri(uri: Uri): Single<Song> {
        return Single.fromCallable { getByUriInternal(uri) }
            .map { it.toLong() }
            .flatMap { getByParam(it).firstOrError() }
    }

    @SuppressLint("Recycle")
    private fun getByUriInternal(uri: Uri): String? {
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            when (uri.authority) {
                "com.android.providers.media.documents" -> return DocumentsContract.getDocumentId(uri).split(":")[1]
                "media" -> return uri.lastPathSegment
            }
        }
        var songFile: File? = null
        if (uri.authority == "com.android.externalstorage.documents") {
            val child = uri.path?.split(":", limit = 2) ?: listOf()
            songFile = File(Environment.getExternalStorageDirectory(), child[1])
        }

        if (songFile == null) {
            getFilePathFromUri(uri)?.let { path ->
                songFile = File(path)
            }
        }
        if (songFile == null && uri.path != null) {
            songFile = File(uri.path)
        }

        var songId: String? = null

        if (songFile != null) {
            context.contentResolver.query(
                MEDIA_STORE_URI, arrayOf(BaseColumns._ID),
                "${MediaStore.Audio.AudioColumns.DATA} = ?",
                arrayOf(songFile!!.absolutePath), null
            )?.let { cursor ->
                cursor.moveToFirst()
                songId = "${cursor.getLong(BaseColumns._ID)}"
                cursor.close()
            }
        }


        return songId
    }

    @SuppressLint("Recycle")
    private fun getFilePathFromUri(uri: Uri): String? {
        var path: String? = null
        context.contentResolver.query(
            uri, arrayOf(MediaStore.Audio.Media.DATA),
            null, null, null
        )?.let { cursor ->
            cursor.moveToFirst()

            path = cursor.getString(MediaStore.Audio.Media.DATA)
            cursor.close()
        }
        return path
    }

    override fun getUneditedByParam(songId: Long): Observable<Song> {
        return rxContentResolver.createQuery(
            MEDIA_STORE_URI, PROJECTION, "${MediaStore.Audio.Media._ID} = ?",
            arrayOf("$songId"), " ${MediaStore.Audio.Media._ID} ASC LIMIT 1", false
        )
            .debounceFirst()
            .lift(SqlBrite.Query.mapToOne { it.toUneditedSong() })
            .distinctUntilChanged()
    }

    override fun getAllUnfiltered(): Observable<List<Song>> {
        return rxContentResolver.createQuery(
            MEDIA_STORE_URI,
            PROJECTION,
            SELECTION,
            null,
            SORT_ORDER,
            false
        )
            .debounceFirst()
            .lift(SqlBrite.Query.mapToList { it.toSong() })
            .doOnError { it.printStackTrace() }
            .onErrorReturnItem(listOf())
    }

    override fun deleteSingle(songId: Long): Completable {
        return Single.fromCallable {
            context.contentResolver.delete(MEDIA_STORE_URI, "${BaseColumns._ID} = ?", arrayOf("$songId"))
        }
            .filter { it > 0 }
            .flatMapSingle { getByParam(songId).firstOrError() }
            .map { File(it.path) }
            .filter { it.exists() }
            .map { it.delete() }
            .toSingle()
            .ignoreElement()

    }

    override fun deleteGroup(songList: List<Song>): Completable {
        return Flowable.fromIterable(songList)
            .map { it.id }
            .flatMapCompletable { deleteSingle(it).subscribeOn(Schedulers.io()) }
    }

}

