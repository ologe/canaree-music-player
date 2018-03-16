package dev.olog.msc.presentation.image.creation

import android.content.Context
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.utils.img.ImageUtils
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.ifNetworkIsAvailable
import dev.olog.msc.utils.media.store.notifySongMediaStore
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.toFlowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SongImageCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val lastFmGateway: LastFmGateway,
        private val imagesThreadPool: ImagesThreadPool
) {

    fun execute(list: List<Song>): Single<*> {
        return Single.fromCallable { list }
                .flattenAsFlowable { it }
                .onBackpressureBuffer()
                .subscribeOn(imagesThreadPool.ioScheduler)
                .filter { notExists(it) }
                .toList()
                .flatMap { fetchImages(it) }
    }

    private fun fetchImages(songs: List<Song>): Single<*> {
        return Flowables.zip(sample(songs.size), songs.toFlowable(), { _, song -> song })
                .onBackpressureBuffer()
                .observeOn(imagesThreadPool.ioScheduler)
                .ifNetworkIsAvailable(ctx, { song, isConnected ->
                    // check for every item if connection is still available, if not, throws an exception
                    if (!isConnected){
                        null
                    } else song
                }).flatMapMaybe {
                    lastFmGateway.getTrack(it.id, it.title, it.artist, it.album)
                            .filter { it.isPresent }
                            .map { it.get() }
                            .flatMap { lastFmGateway.insertTrackImage(it.id, it.image).toMaybe<Boolean>() }
                            .onErrorComplete()
                }
                .buffer(10)
                .map { it.reduce { acc, curr -> acc || curr } }
                .filter { it }
                .doOnNext { notifySongMediaStore(ctx) }
                .toList()
    }

    private fun notExists(song: Song): Boolean {
        if (song.image == ImagesFolderUtils.forAlbum(song.albumId)){
            return !ImageUtils.isRealImage(ctx, song.image)
        } // else already using a downloaded image or a local image
        return false
    }

    private fun sample(times: Int): Flowable<*> {
        return Flowable.interval(1, TimeUnit.SECONDS, imagesThreadPool.ioScheduler)
                .take(times.toLong())
    }

}