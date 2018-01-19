package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Playlists.Members.getContentUri
import com.squareup.sqlbrite3.BriteContentResolver
import dev.olog.data.R
import dev.olog.data.db.AppDatabase
import dev.olog.data.entity.PlaylistMostPlayedEntity
import dev.olog.data.mapper.toPlaylist
import dev.olog.data.mapper.toPlaylistSong
import dev.olog.data.utils.FileUtils
import dev.olog.data.utils.getLong
import dev.olog.domain.entity.Playlist
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaId
import dev.olog.shared_android.Constants
import dev.olog.shared_android.ImagesFolderUtils
import dev.olog.shared_android.assertBackgroundThread
import io.reactivex.*
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        private val favoriteGateway: FavoriteGateway,
        appDatabase: AppDatabase,
        private val helper: PlaylistRepositoryHelper,
        imagesCreator: ImagesCreator

) : PlaylistGateway {

    companion object {
        private val MEDIA_STORE_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
        private val PROJECTION = arrayOf(
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME
        )
        private val SELECTION: String? = null
        private val SELECTION_ARGS: Array<String>? = null
        private const val SORT_ORDER = "lower(${MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER})"

        private val SONG_PROJECTION = arrayOf(
                MediaStore.Audio.Playlists.Members._ID,
                MediaStore.Audio.Playlists.Members.AUDIO_ID
        )
        private val SONG_SELECTION = null
        private val SONG_SELECTION_ARGS: Array<String>? = null
        private const val SONG_SORT_ORDER = MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
    }

    private val resources = context.resources

    private val mostPlayedDao = appDatabase.playlistMostPlayedDao()
    private val historyDao = appDatabase.historyDao()

    private val autoPlaylistTitles = resources.getStringArray(R.array.auto_playlists)

    private fun autoPlaylist() = listOf(
            createAutoPlaylist(Constants.LAST_ADDED_ID, autoPlaylistTitles[0]),
            createAutoPlaylist(Constants.FAVORITE_LIST_ID, autoPlaylistTitles[1]),
            createAutoPlaylist(Constants.HISTORY_LIST_ID, autoPlaylistTitles[2])
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
            }
            .onErrorReturn { listOf() }
            .toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()
            .doOnNext { imagesCreator.subscribe(createImages()) }
            .replay(1)
            .refCount()
            .doOnTerminate { imagesCreator.unsubscribe() }

    private fun getPlaylistSize(playlistId: Long): Int {
        assertBackgroundThread()

        val cursor = contentResolver.query(getContentUri("external", playlistId),
                arrayOf("count(*)"), null, null, null)
        cursor.moveToFirst()
        val size = cursor.getInt(0)
        cursor.close()
        return size
    }

    override fun createImages() : Single<Any> {
        return contentProviderObserver.firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flattenAsFlowable { it }
                .parallel()
                .runOn(Schedulers.io())
                .map { Pair(it, getSongListAlbumsId(it.id)) }
                .map { (playlist, albumsId) -> try {
                    runBlocking { makeImage(this@PlaylistRepository.context, playlist, albumsId).await() }
                } catch (ex: Exception){/*amen*/}
                }.sequential()
                .toList()
                .map { it.contains(true) }
                .onErrorReturnItem(false)
                .doOnSuccess { created ->
                    if (created) {
                        contentResolver.notifyChange(MEDIA_STORE_URI, null)
                    }
                }.map { Unit }
    }

    private fun makeImage(context: Context, playlist: Playlist, albumsId: List<Long>) : Deferred<Boolean> = async {
        val folderName = ImagesFolderUtils.getFolderName(ImagesFolderUtils.PLAYLIST)
        FileUtils.makeImages2(context, albumsId, folderName, "${playlist.id}")
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

    override fun getPlaylistsBlocking(): List<Playlist> {
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
        val result = if (Constants.autoPlaylists.contains(param)){
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
            Constants.LAST_ADDED_ID -> getLastAddedSongs()
            Constants.FAVORITE_LIST_ID -> favoriteGateway.getAll()
            Constants.HISTORY_LIST_ID -> historyDao.getAllAsSongs(songGateway.getAll().firstOrError())
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
        val obs = rxContentResolver.createQuery(
                getContentUri("external", playlistId),
                SONG_PROJECTION,
                SONG_SELECTION,
                SONG_SELECTION_ARGS,
                SONG_SORT_ORDER,
                true

        ).mapToList { it.toPlaylistSong() }
                .toFlowable(BackpressureStrategy.LATEST)
                .flatMapSingle { playlistSongs -> songGateway.getAll().firstOrError().map { songs ->
                            playlistSongs.asSequence()
                            .mapNotNull { playlistSong ->
                                val song = songs.firstOrNull { it.id == playlistSong.songId }
                                song?.copy(trackNumber = playlistSong.idInPlaylist.toInt())
                            }.toList()
                }}

        return Flowable.merge(
                obs.take(1),
                obs.skip(1).debounce(500, TimeUnit.MILLISECONDS) // wrong updates in detail fragment
        )
    }

    override fun getMostPlayed(mediaId: MediaId): Flowable<List<Song>> {
        val playlistId = mediaId.categoryValue.toLong()
        if (Constants.autoPlaylists.contains(playlistId)){
            return Flowable.just(listOf())
        }
        return mostPlayedDao.getAll(playlistId, songGateway.getAll())
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        val songId = mediaId.leaf!!
        val playlistId = mediaId.categoryValue.toLong()
        return songGateway.getByParam(songId)
                .flatMapCompletable { song ->
                    CompletableSource { mostPlayedDao.insertOne(PlaylistMostPlayedEntity(0, song.id, playlistId)) }
                }
    }

    override fun deletePlaylist(playlistId: Long): Completable {
        return helper.deletePlaylist(playlistId)
    }

    override fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Single<String> {
        return helper.addSongsToPlaylist(playlistId, songIds)
    }

    override fun clearPlaylist(playlistId: Long): Completable {
        return Completable.fromCallable { helper.clearPlaylist(playlistId) }
                .subscribeOn(Schedulers.io())
    }

    override fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long) : Completable{
        return helper.removeSongFromPlaylist(playlistId, idInPlaylist)
    }

    override fun createPlaylist(playlistName: String): Single<Long> {
        return helper.createPlaylist(playlistName)
    }

    override fun renamePlaylist(playlistId: Long, newTitle: String): Completable {
        return helper.renamePlaylist(playlistId, newTitle)
    }

    override fun moveItem(playlistId: Long, from: Int, to: Int): Boolean {
        return helper.moveItem(playlistId, from, to)
    }
}