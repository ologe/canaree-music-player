package dev.olog.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.res.Resources
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Playlists.Members.*
import com.squareup.sqlbrite2.BriteContentResolver
import dev.olog.data.DataConstants
import dev.olog.data.R
import dev.olog.data.db.AppDatabase
import dev.olog.data.entity.PlaylistMostPlayedEntity
import dev.olog.data.mapper.toPlaylist
import dev.olog.data.utils.getLong
import dev.olog.domain.entity.Playlist
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.shared.MediaIdHelper
import io.reactivex.*
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
        private val contentResolver: ContentResolver,
        resources: Resources,
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        private val favoriteGateway: FavoriteGateway,
        appDatabase: AppDatabase

) : PlaylistGateway {

    companion object {
        private val MEDIA_STORE_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
        private val PROJECTION = arrayOf(
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
        )
        private val SELECTION: String? = null
        private val SELECTION_ARGS: Array<String>? = null
        private val SORT_ORDER = MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER

        private val SONG_PROJECTION = arrayOf(
                MediaStore.Audio.Playlists.Members._ID,
                MediaStore.Audio.Playlists.Members.AUDIO_ID
        )
        private val SONG_SELECTION = null
        private val SONG_SELECTION_ARGS: Array<String>? = null
        private val SONG_SORT_ORDER = MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
    }

    private val mostPlayedDao = appDatabase.playlistMostPlayedDao()

    private val autoPlaylistsTitle = resources.getStringArray(R.array.auto_playlists)

    private val autoPlaylists = listOf(
            Playlist(DataConstants.LAST_ADDED_ID, autoPlaylistsTitle[0]),
            Playlist(DataConstants.FAVORITE_LIST_ID, autoPlaylistsTitle[1]),
            Playlist(DataConstants.HISTORY_LIST_ID, autoPlaylistsTitle[2])
    )

    private val contentProviderObserver : Flowable<List<Playlist>> = rxContentResolver
            .createQuery(
                    MEDIA_STORE_URI,
                    PROJECTION,
                    SELECTION,
                    SELECTION_ARGS,
                    SORT_ORDER,
                    false
            ).mapToList { it.toPlaylist() }
            .toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()
            .replay(1)
            .refCount()

    override fun getAll(): Flowable<List<Playlist>> = contentProviderObserver
            .map { val result = it.sortedWith(compareBy { it.title.toLowerCase() }).toMutableList()
                result.addAll(0, autoPlaylists)
                result.toList()
            }

    override fun getActualPlaylistsBlocking(): List<Playlist> {
        val cursor = contentResolver.query(MEDIA_STORE_URI, PROJECTION,
                SELECTION, SELECTION_ARGS, SORT_ORDER)
        val list = mutableListOf<Playlist>()
        cursor.use {
            while (it.moveToNext()){
                list.add(cursor.toPlaylist())
            }
        }
        return list
    }

    override fun getByParam(param: Long): Flowable<Playlist> {
        return getAll().flatMapSingle { it.toFlowable()
                .filter { it.id == param }
                .firstOrError()
        }
    }

    override fun observeSongListByParam(playlistId: Long): Flowable<List<Song>> {
        return when (playlistId){
            DataConstants.LAST_ADDED_ID -> getLastAddedSongs()
            DataConstants.FAVORITE_LIST_ID -> favoriteGateway.getAll()
            DataConstants.HISTORY_LIST_ID -> getLastAddedSongs() // todo
            else -> getPlaylistSongs(playlistId)
        }
    }

    private fun getLastAddedSongs() : Flowable<List<Song>>{
        return songGateway.getAll().flatMapSingle {
            it.toFlowable().toSortedList { o1, o2 ->  (o2.dateAdded - o1.dateAdded).toInt() }
        }
    }

    private fun getPlaylistSongs(playlistId: Long) : Flowable<List<Song>> {
        return rxContentResolver.createQuery(
                getContentUri("external", playlistId),
                SONG_PROJECTION,
                SONG_SELECTION,
                SONG_SELECTION_ARGS,
                SONG_SORT_ORDER,
                false

        ).mapToList { it.getLong(MediaStore.Audio.Playlists.Members.AUDIO_ID) }
                .toFlowable(BackpressureStrategy.LATEST)
                .flatMapSingle { it.toFlowable()
                        .flatMapMaybe { songId -> songGateway.getAll()
                                .flatMapIterable { it }
                                .filter { it.id == songId }
                                .firstElement()
                        }.toList()
                }
    }

    override fun getMostPlayed(param: String): Flowable<List<Song>> {
        return mostPlayedDao.getAll(MediaIdHelper.extractCategoryValue(param).toLong(), songGateway.getAll())
    }

    override fun insertMostPlayed(mediaId: String): Completable {
        val songId = MediaIdHelper.extractLeaf(mediaId).toLong()
        val playlistId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
        return songGateway.getByParam(songId)
                .flatMapCompletable { song ->
                    CompletableSource { mostPlayedDao.insertOne(PlaylistMostPlayedEntity(0, song.id, playlistId)) }
                }
    }

    override fun deletePlaylist(id: Long): Completable {
        return Completable.fromCallable{
            contentResolver.delete(
                    MEDIA_STORE_URI,
                    "${BaseColumns._ID} = ?",
                    arrayOf("$id"))
        }
    }

    override fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Single<String> {
        return Single.create<String> { e ->

            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
            val cursor = contentResolver.query(uri, arrayOf("max(${MediaStore.Audio.Playlists.Members.PLAY_ORDER})"),
                    null, null, null)

            var itemInserted = 0

            cursor.use {
                if (cursor.moveToFirst()){
                    var maxId = it.getInt(0) + 1

                    val arrayOf = mutableListOf<ContentValues>()
                    for (songId in songIds) {
                        val values = ContentValues(2)
                        values.put(PLAY_ORDER, maxId++)
                        values.put(AUDIO_ID, songId)
                        arrayOf.add(values)
                    }

                    itemInserted = contentResolver.bulkInsert(uri, arrayOf.toTypedArray())
                } else {
                    e.onError(IllegalArgumentException("invalid playlist id $playlistId"))
                }
            }

            e.onSuccess(itemInserted.toString())
        }
    }

    override fun clearPlaylist(playlistId: Long): Completable {
        return getPlaylistSongs(playlistId)
                .firstOrError()
                .flattenAsFlowable { it }
                .map { it.id }
                .map { songId -> removeSongFromPlaylist(playlistId, songId) }
                .toList()
                .toCompletable()
    }

    private fun removeSongFromPlaylist(playlistId: Long, songId: Long){
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        contentResolver.delete(uri, "${MediaStore.Audio.Playlists.Members.AUDIO_ID} = ?", arrayOf("$songId"))
    }

    override fun createPlaylist(playlistName: String): Single<Long> {
        return Single.create<Long> { e ->
            val added = System.currentTimeMillis()

            val contentValues = ContentValues()
            contentValues.put(MediaStore.Audio.Playlists.NAME, playlistName)
            contentValues.put(MediaStore.Audio.Playlists.DATE_ADDED, added)
            contentValues.put(MediaStore.Audio.Playlists.DATE_MODIFIED, added)

            try {
                val uri = contentResolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, contentValues)

                e.onSuccess(ContentUris.parseId(uri))

            } catch (exception: Exception){
                e.onError(exception)
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun renamePlaylist(id: Long, newTitle: String): Completable {
        return Completable.create { e ->

            val values = ContentValues(1)
            values.put(MediaStore.Audio.Playlists.NAME, newTitle)

            val rowsUpdated = contentResolver.update(MEDIA_STORE_URI,
                    values, "${BaseColumns._ID} = ?", arrayOf("$id"))

            if (rowsUpdated > 0){
                e.onComplete()
            } else {
                e.onError(Throwable("playlist name not updated"))
            }

        }.subscribeOn(Schedulers.io())
    }
}