package dev.olog.msc.presentation.image.creation

import android.content.Context
import android.net.ConnectivityManager
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.utils.img.ImageUtils
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.isSafeNetwork
import dev.olog.msc.utils.media.store.notifySongMediaStore
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Flowables
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AlbumImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val connectivityManager: ConnectivityManager,
        private val lastFmGateway: LastFmGateway,
        private val imagesThreadPool: ImagesThreadPool
) {

    fun execute(pojo: ImageCreatorPojo<Album>): Single<*>{
        return Single.fromCallable { pojo.data }
                .flattenAsFlowable { it }
                .onBackpressureBuffer()
                .subscribeOn(imagesThreadPool.ioScheduler)
                .filter { notExists(it) }
                .toList()
                .flatMap { fetchImages(ImageCreatorPojo(pojo.canUseMobile, it)) }
    }

    private fun fetchImages(pojo: ImageCreatorPojo<Album>): Single<*>{
        val (canUseMobile, albums) = pojo

        return Flowables.zip(sample(albums.size), Flowable.fromIterable(albums),
                { _, album -> album })
                .onBackpressureBuffer()
                .observeOn(imagesThreadPool.ioScheduler)
                .filter { connectivityManager.isSafeNetwork(canUseMobile) }
                .flatMapSingle {
                    lastFmGateway.getAlbum(it.id, it.title, it.artist)
                            .flatMap { lastFmGateway.insertAlbumImage(it.id, it.image)
                                    .toSingle { true }
                                    .onErrorReturn { false }
                            }.onErrorReturn { false }
                }
                .buffer(10)
                .map { it.reduce { acc, curr -> acc || curr } }
                .filter { it }
                .doOnNext { notifySongMediaStore(ctx) }
                .toList()
    }

    private fun notExists(album: Album): Boolean {
        if (album.image == ImagesFolderUtils.forAlbum(album.id)){
            return !ImageUtils.isRealImage(ctx, album.image)
        } // else already using a downloaded image or a local image
        return false
    }

    private fun sample(times: Int): Flowable<*> {
        return Flowable.interval(500, TimeUnit.MILLISECONDS, imagesThreadPool.ioScheduler)
                .take(times.toLong())
    }

}