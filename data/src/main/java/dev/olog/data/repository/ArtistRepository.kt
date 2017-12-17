package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import dev.olog.data.DataConstants
import dev.olog.data.db.AppDatabase
import dev.olog.data.mapper.toAlbum
import dev.olog.data.utils.FileUtils
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.shared.ApplicationContext
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
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
        songGateway: SongGateway,
        private val albumGateway: AlbumGateway,
        appDatabase: AppDatabase

) :ArtistGateway{

    private val lastPlayedDao = appDatabase.lastPlayedArtistDao()

    private val artistsMap : Flowable<MutableMap<Long, MutableList<Song>>> = songGateway.getAllNotDistinct()
            .flatMapSingle { it.toFlowable()
                    .filter { it.artist != DataConstants.UNKNOWN_ARTIST }
                    .collectInto(mutableMapOf<Long, MutableList<Song>>(), { map, song ->
                        if (map.contains(song.artistId)){
                            map[song.artistId]!!.add(song)
                        } else {
                            map.put(song.artistId, mutableListOf(song))
                        }
                    })
            }.replay(1)
            .refCount()

    private val artistAlbumsMap : Flowable<MutableMap<Long, MutableList<Album>>> = songGateway.getAllNotDistinct()
            .flatMapSingle { it.toFlowable()
                    .filter { it.artist != DataConstants.UNKNOWN_ARTIST }
                    .distinct(Song::albumId)
                    .map { it.toAlbum() }
                    .collectInto(mutableMapOf<Long, MutableList<Album>>(), { map, album ->
                        if (map.contains(album.artistId)){
                            map[album.artistId]!!.add(album)
                        } else {
                            map.put(album.artistId, mutableListOf(album))
                        }
                    })
            }.replay(1)
            .refCount()

    private val dataMap: Flowable<MutableMap<Long, Pair<MutableList<Song>, MutableList<Album>>>> =
            Flowables.zip(artistsMap, artistAlbumsMap, { artists, albums ->
                val result = mutableMapOf<Long, Pair<MutableList<Song>, MutableList<Album>>>()
                for (entry in artists.entries) {
                    val key = entry.key
                    result.put(key, Pair(entry.value, albums[key] ?: mutableListOf()))
                }
                result
            }).replay(1)
                    .refCount()

    private val distinctDataMap = dataMap.distinctUntilChanged()

    private val listObservable : Flowable<List<Artist>> = dataMap
            .flatMapSingle { it.entries.toFlowable()
                    .map {
                        val value = it.value
                        val song = value.first[0]
                        val image = FileUtils.artistImagePath(context, song.artistId)
                        val file = File(image)
                        Artist(song.artistId, song.artist, value.first.size, value.second.size,
                                if (file.exists()) image else "")
                    }
                    .toSortedList(compareBy { it.name.toLowerCase() })
            }.distinctUntilChanged()
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
                            .doOnNext { contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null) }
                            .toList()
                    }.subscribe({}, Throwable::printStackTrace)
        }

        return listObservable
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getByParam(artistId: Long): Flowable<Artist> {
        return getAll().map { it.first { it.id == artistId } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(artistId: Long): Flowable<List<Song>> {
        return distinctDataMap.map { it[artistId]!!.first }
    }

    override fun getAlbums(artistId: Long): Flowable<List<Album>> {


        return distinctDataMap.map { it[artistId]!!.second }
                .flatMapSingle { it.toFlowable()
                        .flatMapMaybe { albumGateway.getByParam(it.id).firstElement() }
                        .toList()
                        .onErrorReturn { listOf() }
                }
    }

    override fun getLastPlayed(): Flowable<List<Artist>> = lastPlayedDao.getAll()
            .map { it.sortedWith(compareByDescending { it.dateAdded }) }
            .flatMapSingle { it.toFlowable()
                    .map {
                        val image = FileUtils.artistImagePath(context, it.id)
                        val file = File(image)
                        Artist(it.id, it.name, image = if (file.exists()) image else "" )
                    }
                    .toList()
            }

    override fun addLastPlayed(item: Artist): Completable {
        return lastPlayedDao.insertOne(item)
    }

}