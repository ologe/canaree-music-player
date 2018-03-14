package dev.olog.msc.presentation.image.creation

import android.content.Context
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.utils.k.extension.ifNetworkIsAvailable
import dev.olog.msc.utils.media.store.notifyArtistMediaStore
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Flowables
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ArtistImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val lastFmGateway: LastFmGateway,
        private val imagesThreadPool: ImagesThreadPool

) {

    fun execute(list: List<Artist>) : Single<*> {
        return Single.fromCallable { list }
                .flattenAsFlowable { it }
                .subscribeOn(imagesThreadPool.ioScheduler)
                .onBackpressureBuffer()
                .flatMapMaybe { artist -> lastFmGateway
                        .shouldFetchArtist(artist.id)
                        .filter { it }
                        .map { artist }
                        .onErrorComplete()
                }.toList()
                .flatMap { fetchImages(it) }
    }

    private fun fetchImages(artists: List<Artist>): Single<*> {
        return Flowables.zip(sample(artists.size), Flowable.fromIterable(artists),
                { _, artist -> artist } )
                .onBackpressureBuffer()
                .observeOn(imagesThreadPool.ioScheduler)
                .ifNetworkIsAvailable(ctx) {  artist, isConnected ->
                    // check for every item if connection is still available, if not, throws an exception
                    if (!isConnected){
                        null
                    } else artist
                }.flatMapSingle {
                    lastFmGateway.getArtist(it.id, it.name).onErrorReturnItem(false)
                }
                .buffer(10)
                .map { it.reduce { acc, curr -> acc || curr } }
                .filter { it }
                .doOnNext { notifyArtistMediaStore(ctx) }
                .toList()
    }

    /*
     Do not use interval below 500(2 times a second) due to lastFm
     limit to 5 request per second
  */
    private fun sample(times: Int): Flowable<*>{
        return Flowable.interval(500, TimeUnit.MILLISECONDS, imagesThreadPool.ioScheduler)
                .take(times.toLong())
    }

}