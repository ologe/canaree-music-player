package dev.olog.data.repository

import android.content.res.Resources
import android.provider.BaseColumns
import android.provider.MediaStore
import com.squareup.sqlbrite2.BriteContentResolver
import dev.olog.data.DataConstants
import dev.olog.data.R
import dev.olog.data.db.AppDatabase
import dev.olog.data.entity.PlaylistMostPlayedEntity
import dev.olog.data.mapper.extractId
import dev.olog.data.mapper.toPlaylist
import dev.olog.domain.entity.Playlist
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.PlaylistGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.shared.MediaIdHelper
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
        resources: Resources,
        private val contentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
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

        private val SONG_PROJECTION = arrayOf(BaseColumns._ID)
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

    private val contentProviderObserver : Flowable<List<Playlist>> = contentResolver
            .createQuery(
                    MEDIA_STORE_URI,
                    PROJECTION,
                    SELECTION,
                    SELECTION_ARGS,
                    SORT_ORDER,
                    false
            ).mapToList { it.toPlaylist() }
            .map {
                val result = it.sortedWith(compareBy { it.title.toLowerCase() }).toMutableList()
                result.addAll(autoPlaylists)
                result.toList()
            }
            .toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()
            .replay(1)
            .refCount()

    override fun getAll(): Flowable<List<Playlist>> = contentProviderObserver

    override fun getByParam(param: Long): Flowable<Playlist> {
        return getAll().flatMapSingle { it.toFlowable()
                .filter { it.id == param }
                .firstOrError()
        }
    }

    override fun observeSongListByParam(param: Long): Flowable<List<Song>> {
        return when (param){
            DataConstants.LAST_ADDED_ID -> getLastAddedSongs()
            DataConstants.FAVORITE_LIST_ID -> getLastAddedSongs() // todo
            DataConstants.HISTORY_LIST_ID -> getLastAddedSongs() // todo
            else -> getPlaylistSongs(param)
        }
    }

    private fun getLastAddedSongs() : Flowable<List<Song>>{
        return songGateway.getAll().flatMapSingle {
            it.toFlowable().toSortedList { o1, o2 ->  (o2.dateAdded - o1.dateAdded).toInt() }
        }
    }

    private fun getPlaylistSongs(playlistId: Long) : Flowable<List<Song>> {
        return contentResolver.createQuery(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                SONG_PROJECTION,
                SONG_SELECTION,
                SONG_SELECTION_ARGS,
                SONG_SORT_ORDER,
                false

        ).mapToList { it.extractId() }
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
}