package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import com.squareup.sqlbrite2.BriteContentResolver
import dev.olog.data.db.AppDatabase
import dev.olog.data.mapper.toArtist
import dev.olog.data.utils.FileUtils
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.shared.ApplicationContext
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.Constants
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        private val albumGateway: AlbumGateway,
        appDatabase: AppDatabase

) : ArtistGateway{

    companion object {
        private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
    }

    private val lastPlayedDao = appDatabase.lastPlayedArtistDao()

    private var imageDisposable : Disposable? = null

    private val contentProviderObserver : Flowable<List<Artist>> = rxContentResolver
            .createQuery(
                    MEDIA_STORE_URI,
                    arrayOf("count(*)"),
                    null, null, null,
                    false
            ).mapToOne { 0 }
            .toFlowable(BackpressureStrategy.LATEST)
            .flatMap { songGateway.getAll() }
            .map { songList -> songList.asSequence()
                        .filter { it.artist != Constants.UNKNOWN_ARTIST }
                        .distinctBy { it.artistId }
                        .map { song ->
                            val albums = songList.asSequence()
                                    .distinctBy { it.albumId }
                                    .count { it.artistId == song.artistId }
                            val songs = songList.count { it.artistId == song.artistId }

                            song.toArtist(context, songs, albums)
                        }.sortedBy { it.name.toLowerCase() }
                        .toList()

            }.distinctUntilChanged()
            .doOnNext { subscribeToImageCreation() }
            .replay(1)
            .refCount()
            .doOnTerminate { imageDisposable.unsubscribe() }

    private val albumsMap : MutableMap<Long, Flowable<List<Album>>> = mutableMapOf()
    private val songMap : MutableMap<Long, Flowable<List<Song>>> = mutableMapOf()

    private fun subscribeToImageCreation(){
        imageDisposable.unsubscribe()
        imageDisposable = createImages().subscribe({}, Throwable::printStackTrace)
    }

    override fun createImages() : Single<Any> {
        return songGateway.getAllForImageCreation()
                .map { it.groupBy { it.artistId } }
                .flatMap { it.entries.toFlowable()
                        .parallel()
                        .runOn(Schedulers.io())
                        .map { map -> FileUtils.makeImages(context, map.value, "artist",
                                "${map.key}") }
                        .sequential()
                        .buffer(10)
                        .doOnNext { contentResolver.notifyChange(MEDIA_STORE_URI, null) }
                        .toList()

                }
    }

    override fun getAll(): Flowable<List<Artist>> {

        return contentProviderObserver
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getByParam(artistId: Long): Flowable<Artist> {
        return getAll().map { it.first { it.id == artistId } }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(artistId: Long): Flowable<List<Song>> {
        var flowable = songMap[artistId]

        if (flowable == null){
            flowable = songGateway.getAll().map {
                it.asSequence().filter { it.artistId == artistId }.toList()
            }.distinctUntilChanged()
                    .replay(1)
                    .refCount()

            songMap[artistId] = flowable
        }

        return flowable
    }

    override fun getAlbums(artistId: Long): Flowable<List<Album>> {
        var flowable = albumsMap[artistId]

        if (flowable == null){
            flowable = albumGateway.getAll()
                    .map { it.filter { it.artistId == artistId } }
                    .distinctUntilChanged()
                    .replay(1)
                    .refCount()

            albumsMap[artistId] = flowable
        }

        return flowable
    }

    override fun getLastPlayed(): Flowable<List<Artist>> {
        return Flowables.combineLatest(getAll(), lastPlayedDao.getAll(), { all, lastPlayed ->
            lastPlayed.asSequence()
                    .map { lastPlayedArtistEntity -> all.firstOrNull { it.id == lastPlayedArtistEntity.id } }
                    .filter { it != null }
                    .map { it!! }
                    .take(10)
                    .toList()
        })
    }

    override fun addLastPlayed(item: Artist): Completable = lastPlayedDao.insertOne(item)

}