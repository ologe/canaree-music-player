package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import dev.olog.data.DataConstants
import dev.olog.data.db.AppDatabase
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.domain.mapper.toAlbum
import dev.olog.shared.ApplicationContext
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.toFlowable
import java.io.File
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

    private val artistsMap : Flowable<MutableMap<Long, MutableList<Song>>> = songGateway.getAll()
            .flatMapSingle { it.toFlowable()
                    .filter { it.artist != DataConstants.UNKNOWN_ARTIST }
                    .collectInto(mutableMapOf<Long, MutableList<Song>>(), { map, song ->
                        if (map.contains(song.artistId)){
                            map[song.artistId]!!.add(song)
                        } else {
                            map.put(song.artistId, mutableListOf(song))
                        }
                    })
            }.distinctUntilChanged()
            .replay(1)
            .refCount()

    private val artistAlbumsMap : Flowable<MutableMap<Long, MutableList<Album>>> = songGateway.getAll()
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
            }.distinctUntilChanged()
            .replay(1)
            .refCount()

    private val dataMap: Flowable<MutableMap<Long, Pair<MutableList<Song>, MutableList<Album>>>> =
            Flowables.zip(artistsMap, artistAlbumsMap, { artists, albums ->
                val result = mutableMapOf<Long, Pair<MutableList<Song>, MutableList<Album>>>()
                for (entry in artists.entries) {
                    val key = entry.key
                    result.put(key, Pair(entry.value, albums[key] ?: mutableListOf()))
                }
                result
            }).distinctUntilChanged()
            .replay(1)
            .refCount()

    private val listObservable : Flowable<List<Artist>> = dataMap
            .flatMapSingle { it.entries.toFlowable()
                    .map {
                        val value = it.value
                        val song = value.first[0]

                        Artist(song.artistId, song.artist, value.first.size, value.second.size,
                                getImagePath(song.artistId))
                    }
                    .toSortedList(compareBy { it.name.toLowerCase() })
            }.distinctUntilChanged()
            .replay(1)
            .refCount()

    private var imagesCreated = false

    private fun getImagePath(artistId: Long): String {
        val dataDir = "${context.applicationInfo.dataDir}${File.separator}artist${File.separator}"
        return "$dataDir$artistId"
    }

//    private val images = dataMap
//            .firstOrError()
//            .flatMap { it.entries.toFlowable()
//                    .map { it.value }
//                    .flatMapSingle { (songList, _) -> songList.toFlowable()
//                            .map { it.albumId }
//                            .map { ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), it) }
//                            .map { uri -> try {
//                                Option(MediaStore.Images.Media.getBitmap(contentResolver, uri))
//                            } catch (ex: Exception){
//                                Option(null)
//                            }}
//                            .filter { it.item != null }
//                            .map { it.item!! }
//                            .take(4)
//                            .toList()
//                            .map { ImageUtils.joinImages(it) }
//                            .map {
//                                val song = songList[0]
//                                FileUtils.saveFile(context, "artist", song.artist,it)
//                            }
//                            .subscribeOn(Schedulers.io())
//                    }.toList()
//            }.subscribeOn(Schedulers.io())
//            .toCompletable()

    override fun getAll(): Flowable<List<Artist>> {

//        if (!imagesCreated){
//            imagesCreated = true
//            images.subscribe({
//                contentResolver.notifyChange(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null)
//
//            }, Throwable::printStackTrace)
//        }

        return listObservable
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getByParam(artistId: Long): Flowable<Artist> {
        return getAll().map { it.first { it.id == artistId } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(artistId: Long): Flowable<List<Song>> {
        return dataMap.map { it[artistId]!!.first }
    }

    override fun getAlbums(artistId: Long): Flowable<List<Album>> {
        return dataMap.map { it[artistId]!!.second }
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
                        Artist(it.id, it.name, image = getImagePath(it.id))
                    }
                    .toList()
            }

    override fun addLastPlayed(item: Artist): Completable {
        return lastPlayedDao.insertOne(item)
    }

}