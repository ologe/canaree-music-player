package dev.olog.data.repository

import android.provider.MediaStore
import com.squareup.sqlbrite2.BriteContentResolver
import dev.olog.data.DataConstants
import dev.olog.data.db.AppDatabase
import dev.olog.data.mapper.toAlbum
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.gateway.SongGateway
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepository @Inject constructor(
        rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase

) : AlbumGateway {

    companion object {
        private val MEDIA_STORE_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        private val PROJECTION = arrayOf(
                MediaStore.Audio.Albums.ALBUM_ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS
        )

        private val SELECTION = null
        private val SELECTION_ARGS = null
        private val SORT_ORDER = "lower(${MediaStore.Audio.Albums.ALBUM})"
    }

    private val lastPlayedDao = appDatabase.lastPlayedAlbumDao()

    private val songListMap : MutableMap<Long, Flowable<List<Song>>> = mutableMapOf()

    private val contentProviderObserver = rxContentResolver
            .createQuery(
                    MEDIA_STORE_URI,
                    PROJECTION,
                    SELECTION,
                    SELECTION_ARGS,
                    SORT_ORDER,
                    false
            ).mapToList { it.toAlbum() }
            .toFlowable(BackpressureStrategy.LATEST)
            .replay(1)
            .refCount()

    override fun getAll(): Flowable<List<Album>> = contentProviderObserver

    override fun getByParam(param: Long): Flowable<Album> {
        return getAll().map { it.first { it.id == param } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(albumId: Long): Flowable<List<Song>> {
        var songListFlowable = songListMap[albumId]
        if (songListFlowable == null){
            songListFlowable = songGateway.getAll()
                    .flatMapSingle { it.toFlowable()
                            .filter { it.artist != DataConstants.UNKNOWN_ALBUM }
                            .filter { it.albumId == albumId }
                            .toList()
                    }.distinctUntilChanged()
                    .replay(1)
                    .refCount()

            songListMap[albumId] = songListFlowable
        }
        return songListFlowable
    }

    override fun getLastPlayed(): Flowable<List<Album>> = lastPlayedDao.getAll()
            .map { it.sortedWith(compareByDescending { it.dateAdded }) }
            .flatMapSingle { it.toFlowable()
                    .map { Album(it.id, it.artistId, it.title, it.artist, it.image) }
                    .toList()
            }

    override fun addLastPlayed(item: Album): Completable {
        return lastPlayedDao.insertOne(item)
    }
}