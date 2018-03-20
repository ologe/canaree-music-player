package dev.olog.msc.presentation.image.creation

import android.content.Context
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.gateway.LastFmGateway
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AlbumImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val lastFmGateway: LastFmGateway,
        private val imagesThreadPool: ImagesThreadPool
) {

    fun execute(list: List<Album>): Single<*> {
        return Single.just("")
//        return Single.fromCallable { list }
//                .flattenAsFlowable { it }
//                .onBackpressureBuffer()
//                .subscribeOn(imagesThreadPool.ioScheduler)
////                .filter { notExists(it) }
//                .toList()
//                .flatMap { fetchImages(it) }
    }

    private fun fetchImages(albums: List<Album>): Single<*>{
        return Single.just("")

//        return Flowables.zip(sample(albums.size), albums.toFlowable(), { _, album -> album })
//                .onBackpressureBuffer()
//                .observeOn(imagesThreadPool.ioScheduler)
//                .ifNetworkIsAvailable(ctx) { artist, isConnected ->
//                    // check for every item if connection is still available, if not, throws an exception
//                    if (!isConnected){
//                        null
//                    } else artist
//
//                }.flatMapMaybe {
//                    lastFmGateway.getAlbum(it.id)
//                            .filter { it.isPresent }
//                            .map { it.get() }
////                            .flatMap { lastFmGateway.insertAlbumImage(it.id, it.image).toMaybe<Boolean>() }
//                            .onErrorComplete()
//                }
//                .buffer(10)
//                .map { it.reduce { acc, curr -> acc || curr } }
//                .filter { it }
//                .doOnNext { notifySongMediaStore(ctx) }
//                .toList()
    }



    private fun sample(times: Int): Flowable<*> {
        return Flowable.interval(1, TimeUnit.SECONDS, imagesThreadPool.ioScheduler)
                .take(times.toLong())
    }

}