package dev.olog.msc.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import dev.olog.msc.R
import dev.olog.msc.constants.PlaylistConstants
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.PlaylistMostPlayedEntity
import dev.olog.msc.data.mapper.extractId
import dev.olog.msc.data.mapper.toPlaylist
import dev.olog.msc.data.mapper.toPlaylistSong
import dev.olog.msc.data.repository.util.CommonQuery
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.crashlyticsLog
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toFlowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

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

class PlaylistRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        private val favoriteGateway: FavoriteGateway,
        appDatabase: AppDatabase,
        private val helper: PlaylistRepositoryHelper,
        private val appPrefsUseCase: AppPreferencesUseCase

) : PlaylistGateway {

    private val resources = context.resources

    private val mostPlayedDao = appDatabase.playlistMostPlayedDao()
    private val historyDao = appDatabase.historyDao()

    private val autoPlaylistTitles = resources.getStringArray(R.array.common_auto_playlists)

    private fun createAutoPlaylist(id: Long, title: String, listSize: Int) : Playlist {
        // todo auto playlist image
        return Playlist(id, title, listSize, "")
    }

    private fun queryAllData(): Observable<List<Playlist>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, PROJECTION, SELECTION,
                SELECTION_ARGS, SORT_ORDER, false
        ).mapToList {
            val id = it.extractId()
            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id)
            val size = CommonQuery.getSize(contentResolver, uri)
            it.toPlaylist(context, size)
        }.map { removeBlacklisted(it) }
                .onErrorReturnItem(listOf())
    }

    private val cachedData = queryAllData()
            .replay(1)
            .refCount()
            .throttleLast(350, TimeUnit.MILLISECONDS)

    private fun removeBlacklisted(list: MutableList<Playlist>): List<Playlist>{
        val songsIds = CommonQuery.getAllSongsIdNotBlackListd(contentResolver, appPrefsUseCase)
        for (playlist in list.toList()) {
            val newSize = calculateNewPlaylistSize(playlist.id, songsIds)
            list[list.indexOf(playlist)] = playlist.copy(size = newSize)
        }
        return list
    }

    private fun calculateNewPlaylistSize(id: Long, songIds: List<Long>): Int {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id)
        val cursor = contentResolver.query(uri, arrayOf(MediaStore.Audio.Playlists.Members.AUDIO_ID), null, null, null)
        val list = mutableListOf<Long>()
        while (cursor.moveToNext()){
            list.add(cursor.getLong(0))
        }
        cursor.close()
        list.retainAll(songIds)

        return list.size
    }

    override fun getAll(): Observable<List<Playlist>> {
        return cachedData
    }

    override fun getAllNewRequest(): Observable<List<Playlist>> {
        return queryAllData()
    }

    override fun getByParam(param: Long): Observable<Playlist> {
        val result = if (PlaylistConstants.isAutoPlaylist(param)){
            getAllAutoPlaylists()
        } else getAll()

        return result.map { playlists ->
            try {
                playlists.first { it.id == param }
            } catch (ex: Exception){
                crashlyticsLog("searched playlist=$param, all playlists id=${playlists.map { it.id }}")
                throw ex
            }
        }
    }

    override fun getAllAutoPlaylists(): Observable<List<Playlist>> {
        return Observables.combineLatest(
                songGateway.getAll().map { it.count() }.distinctUntilChanged(), // last added
                favoriteGateway.getAll().map { it.count() }.distinctUntilChanged(), // favorites
                historyDao.getAllAsSongs(songGateway.getAll().firstOrError()).map { it.count() }, // history
                { last, favorites, history -> listOf(
                        createAutoPlaylist(PlaylistConstants.LAST_ADDED_ID, autoPlaylistTitles[0], last),
                        createAutoPlaylist(PlaylistConstants.FAVORITE_LIST_ID, autoPlaylistTitles[1], favorites),
                        createAutoPlaylist(PlaylistConstants.HISTORY_LIST_ID, autoPlaylistTitles[2], history)
                ) }
        )
    }

    override fun insertSongToHistory(songId: Long): Completable {
        return historyDao.insert(songId)
    }

    override fun getPlaylistsBlocking(): List<Playlist> {
        val cursor = contentResolver.query(MEDIA_STORE_URI, PROJECTION,
                SELECTION, SELECTION_ARGS, SORT_ORDER)
        val list = mutableListOf<Playlist>()
        while (cursor.moveToNext()){
            list.add(cursor.toPlaylist(context, -1))
        }
        cursor.close()
        return list
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(playlistId: Long): Observable<List<Song>> {
        return when (playlistId){
            PlaylistConstants.LAST_ADDED_ID -> getLastAddedSongs()
            PlaylistConstants.FAVORITE_LIST_ID -> favoriteGateway.getAll()
            PlaylistConstants.HISTORY_LIST_ID -> historyDao.getAllAsSongs(songGateway.getAll().firstOrError())
            else -> getPlaylistSongs(playlistId)
        }
    }

    private fun getLastAddedSongs() : Observable<List<Song>>{
        return songGateway.getAll().flatMapSingle {
            it.toFlowable().toSortedList { o1, o2 ->  (o2.dateAdded - o1.dateAdded).toInt() }
        }
    }

    private fun getPlaylistSongs(playlistId: Long) : Observable<List<Song>> {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)

        return rxContentResolver.createQuery(
                uri, SONG_PROJECTION, SONG_SELECTION,
                SONG_SELECTION_ARGS, SONG_SORT_ORDER, false

        ).mapToList { it.toPlaylistSong() }
                .flatMapSingle { playlistSongs -> songGateway.getAll().firstOrError().map { songs ->
                    playlistSongs.asSequence()
                            .mapNotNull { playlistSong ->
                                val song = songs.firstOrNull { it.id == playlistSong.songId }
                                song?.copy(trackNumber = playlistSong.idInPlaylist.toInt())
                            }.toList()
                }}
    }

    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> {
        val playlistId = mediaId.categoryValue.toLong()
        if (PlaylistConstants.isAutoPlaylist(playlistId)){
            return Observable.just(listOf())
        }
        return mostPlayedDao.getAll(playlistId, songGateway.getAll())
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        val songId = mediaId.leaf!!
        val playlistId = mediaId.categoryValue.toLong()
        return songGateway.getByParam(songId)
                .firstOrError()
                .flatMapCompletable { song ->
                    CompletableSource { mostPlayedDao.insertOne(PlaylistMostPlayedEntity(0, song.id, playlistId)) }
                }
    }

    override fun deletePlaylist(playlistId: Long): Completable {
        return helper.deletePlaylist(playlistId)
    }

    override fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable {
        return Completable.fromCallable { helper.addSongsToPlaylist(playlistId, songIds) }
    }

    override fun clearPlaylist(playlistId: Long): Completable {
        return helper.clearPlaylist(playlistId)
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

    override fun removeDuplicated(playlistId: Long): Completable {
        return Completable.fromCallable { helper.removeDuplicated(playlistId) }
    }
}