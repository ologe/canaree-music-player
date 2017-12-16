package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.BaseColumns
import android.provider.MediaStore
import com.squareup.sqlbrite2.BriteContentResolver
import dev.olog.data.DataConstants
import dev.olog.data.db.AppDatabase
import dev.olog.data.mapper.toArtist
import dev.olog.data.mapper.toArtistsAlbum
import dev.olog.data.utils.FileUtils
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.gateway.FolderGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.shared.ApplicationContext
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        private val folderGateway: FolderGateway,
        appDatabase: AppDatabase

) :ArtistGateway {

    companion object {
        private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

        private val PROJECTION = arrayOf(
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        )

        private val SELECTION = "${MediaStore.Audio.Artists.ARTIST} <> ?"
        private val SELECTION_ARGS = arrayOf("<unknown>")
        private val SORT_ORDER = "lower(${MediaStore.Audio.Artists.ARTIST})"

        private val ALBUM_PROJECTION = arrayOf(
                BaseColumns._ID,
                MediaStore.Audio.Artists.Albums.ALBUM,
                MediaStore.Audio.Artists.Albums.ARTIST,
                MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS
        )
        private val ALBUM_SELECTION = null
        private val ALBUM_SELECTION_ARGS: Array<String>? = null
        private val ALBUM_SORT_ORDER = "lower(${MediaStore.Audio.Artists.Albums.ALBUM})"
    }

    private val lastPlayedDao = appDatabase.lastPlayedArtistDao()

    private val songListMap : MutableMap<Long, Flowable<List<Song>>> = mutableMapOf()

    private val albumListMap : MutableMap<Long, Flowable<List<Album>>> = mutableMapOf()

    private val contentProviderObserver : Flowable<List<Artist>> = rxContentResolver
            .createQuery(
                    MEDIA_STORE_URI,
                    PROJECTION,
                    SELECTION,
                    SELECTION_ARGS,
                    SORT_ORDER,
                    false
            ).mapToList { it.toArtist(context) }
            .toFlowable(BackpressureStrategy.LATEST)
            .flatMapSingle { artistList ->
                folderGateway.getAll()
                        .firstOrError()
                        .map { folders -> artistList }
            }
            .replay(1)
            .refCount()

    private val imagesCreated = AtomicBoolean(false)

    override fun getAll(): Flowable<List<Artist>> {
        val compareAndSet = imagesCreated.compareAndSet(false, true)
        if (compareAndSet){
            getAll().firstOrError()
                    .flatMap { it.toFlowable()
                            .flatMapMaybe { artist -> FileUtils.makeImages(context,
                                    observeSongListByParam(artist.id), "artist", "${artist.id}")
                                    .subscribeOn(Schedulers.io())
                            }.subscribeOn(Schedulers.io())
                            .buffer(5)
                            .doOnNext { contentResolver.notifyChange(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null) }
                            .toList()
                    }.subscribe({}, Throwable::printStackTrace)
        }

        return contentProviderObserver
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getByParam(artistId: Long): Flowable<Artist> {
        return getAll().map { it.first { it.id == artistId } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(artistId: Long): Flowable<List<Song>> {
        var songListFlowable = songListMap[artistId]
        if (songListFlowable == null){
            songListFlowable = songGateway.getAll()
                    .flatMapSingle { it.toFlowable()
                            .filter { it.artist != DataConstants.UNKNOWN_ARTIST }
                            .filter { it.artistId == artistId }
                            .toList()
                    }.distinctUntilChanged()
                    .replay(1)
                    .refCount()

            songListMap[artistId] = songListFlowable
        }
        return songListFlowable
    }

    override fun getAlbums(artistId: Long): Flowable<List<Album>> {
        var albumsListFlowable = albumListMap[artistId]

        if (albumsListFlowable == null){
            albumsListFlowable = rxContentResolver.createQuery(
                    MediaStore.Audio.Artists.Albums.getContentUri("external", artistId),
                    ALBUM_PROJECTION,
                    ALBUM_SELECTION,
                    ALBUM_SELECTION_ARGS,
                    ALBUM_SORT_ORDER,
                    false
            ).mapToList { it.toArtistsAlbum(artistId) }
                    .toFlowable(BackpressureStrategy.LATEST)
                    .distinctUntilChanged()
                    .replay(1)
                    .refCount()

            albumListMap[artistId] = albumsListFlowable
        }

        return albumsListFlowable
    }

    override fun getLastPlayed(): Flowable<List<Artist>> = lastPlayedDao.getAll()
            .map { it.sortedWith(compareByDescending { it.dateAdded }) }
            .flatMapSingle { it.toFlowable()
                    .map {
                        val image = FileUtils.artistImagePath(context, it.id)
                        val file = File(image)
                        Artist(it.id, it.name, image = if (file.exists()) image else "")
                    }.toList()
            }

    override fun addLastPlayed(item: Artist): Completable {
        return lastPlayedDao.insertOne(item)
    }

}