package dev.olog.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Playlists.Members.*
import com.squareup.sqlbrite2.BriteContentResolver
import dev.olog.data.R
import dev.olog.data.db.AppDatabase
import dev.olog.data.entity.PlaylistMostPlayedEntity
import dev.olog.data.mapper.toPlaylist
import dev.olog.data.utils.FileUtils
import dev.olog.data.utils.assertBackgroundThread
import dev.olog.data.utils.getLong
import dev.olog.domain.entity.Playlist
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.constants.DataConstants
import dev.olog.shared.unsubscribe
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
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

    private val resources = context.resources

    private var imageDisposable : Disposable? = null

    private val mostPlayedDao = appDatabase.playlistMostPlayedDao()
    private val historyDao = appDatabase.historyDao()

    private val autoPlaylistTitle = resources.getStringArray(R.array.auto_playlists)

    private fun autoPlaylist() = listOf(
            createAutoPlaylist(DataConstants.LAST_ADDED_ID, autoPlaylistTitle[0]),
            createAutoPlaylist(DataConstants.FAVORITE_LIST_ID, autoPlaylistTitle[1]),
            createAutoPlaylist(DataConstants.HISTORY_LIST_ID, autoPlaylistTitle[2])
    )

    private fun createAutoPlaylist(id: Long, title: String) : Playlist {
//        val image = FileUtils.playlistImagePath(context, id) todo
//        val file = File(image)
        return Playlist(id, title, -1, "")
    }

    private val contentProviderObserver : Flowable<List<Playlist>> = rxContentResolver
            .createQuery(
                    MEDIA_STORE_URI,
                    PROJECTION,
                    SELECTION,
                    SELECTION_ARGS,
                    SORT_ORDER,
                    false
            ).mapToList {
                val playlistSize = getPlaylistSize(it.getLong(BaseColumns._ID))
                it.toPlaylist(context, playlistSize)
            }.toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()
            .doOnNext { createImages() }
            .replay(1)
            .refCount()
            .doOnTerminate { imageDisposable.unsubscribe() }

    private fun getPlaylistSize(playlistId: Long): Int {
        assertBackgroundThread()

        val cursor = contentResolver.query(getContentUri("external", playlistId),
                arrayOf("count(*)"), null, null, null)
        cursor.moveToFirst()
        val size = cursor.getInt(0)
        cursor.close()
        return size
    }

    override fun createImages(){

        imageDisposable.unsubscribe()

        imageDisposable = contentProviderObserver.firstOrError()
                .flatMap { it.toFlowable()
                        .parallel()
                        .runOn(Schedulers.io())
                        .map { Pair(it, getSongListAlbumsId(it.id)) }
                        .map { (playlist, albumsId) -> FileUtils.makeImages2(context, albumsId, "playlist", "${playlist.id}") }
                        .sequential()
                        .toList()
                        .doOnSuccess { contentResolver.notifyChange(MEDIA_STORE_URI, null) }
                }.subscribe({}, Throwable::printStackTrace)
    }

    override fun getAll(): Flowable<List<Playlist>> {
        return contentProviderObserver
                .map { it.sortedWith(compareBy { it.title.toLowerCase() }) }
    }

    override fun getAllAutoPlaylists(): Flowable<List<Playlist>> {
        return Flowable.just(autoPlaylist())
                .flatMapSingle { it.toFlowable().toSortedList(compareByDescending { it.id }) }
    }

    override fun insertSongToHistory(songId: Long): Completable {
        return historyDao.insert(songId)
    }

    override fun getActualPlaylistsBlocking(): List<Playlist> {
        val cursor = contentResolver.query(MEDIA_STORE_URI, PROJECTION,
                SELECTION, SELECTION_ARGS, SORT_ORDER)
        val list = mutableListOf<Playlist>()
        cursor.use {
            while (it.moveToNext()){
                list.add(cursor.toPlaylist(context, -1))
            }
        }
        return list
    }

    override fun getByParam(param: Long): Flowable<Playlist> {
        val result = if (DataConstants.autoPlaylists.contains(param)){
            getAllAutoPlaylists()
        } else getAll()

        return result.flatMapSingle { it.toFlowable()
                .filter { it.id == param }
                .firstOrError()
        }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(playlistId: Long): Flowable<List<Song>> {
        return when (playlistId){
            DataConstants.LAST_ADDED_ID -> getLastAddedSongs()
            DataConstants.FAVORITE_LIST_ID -> favoriteGateway.getAll()
            DataConstants.HISTORY_LIST_ID -> historyDao.getAllAsSongs(songGateway.getAll().firstOrError())
            else -> getPlaylistSongs(playlistId)
        }
    }

    private fun getSongListAlbumsId(playlistId: Long): List<Long> {
        val result = mutableListOf<Long>()

        val cursor = contentResolver.query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                arrayOf(MediaStore.Audio.Playlists.Members.ALBUM_ID), null, null, null)
        while (cursor.moveToNext()){
            result.add(cursor.getLong(0))
        }
        cursor.close()
        return result
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
                .flatMapSingle { ids -> songGateway.getAll().firstOrError().map { songs ->
                    ids.asSequence()
                            .map { id -> songs.firstOrNull { it.id == id } }
                            .filter { it != null }
                            .map { it!! }
                            .toList()
                }}
    }

    override fun getMostPlayed(param: String): Flowable<List<Song>> {
        val playlistId = MediaIdHelper.extractCategoryValue(param).toLong()
        if (DataConstants.autoPlaylists.contains(playlistId)){
            return Flowable.just(listOf())
        }
        return mostPlayedDao.getAll(playlistId, songGateway.getAll())
    }

    override fun insertMostPlayed(mediaId: String): Completable {
        val songId = MediaIdHelper.extractLeaf(mediaId).toLong()
        val playlistId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
        return songGateway.getByParam(songId)
                .flatMapCompletable { song ->
                    CompletableSource { mostPlayedDao.insertOne(PlaylistMostPlayedEntity(0, song.id, playlistId)) }
                }
    }

    override fun deletePlaylist(playlistId: Long): Completable {
        return Completable.fromCallable{
            contentResolver.delete(
                    MEDIA_STORE_URI,
                    "${BaseColumns._ID} = ?",
                    arrayOf("$playlistId"))
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

                    contentResolver.notifyChange(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null)

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

    override fun renamePlaylist(playlistId: Long, newTitle: String): Completable {
        return Completable.create { e ->

            val values = ContentValues(1)
            values.put(MediaStore.Audio.Playlists.NAME, newTitle)

            val rowsUpdated = contentResolver.update(MEDIA_STORE_URI,
                    values, "${BaseColumns._ID} = ?", arrayOf("$playlistId"))

            if (rowsUpdated > 0){
                e.onComplete()
            } else {
                e.onError(Throwable("playlist name not updated"))
            }

        }.subscribeOn(Schedulers.io())
    }

    override fun moveItem(playlistId: Long, from: Int, to: Int): Boolean {
        return MediaStore.Audio.Playlists.Members.moveItem(contentResolver, playlistId, from, to)
    }
}