package dev.olog.msc.presentation.image.creation

import android.content.Context
import android.net.ConnectivityManager.*
import android.net.NetworkInfo
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.utils.img.ImageUtils
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.k.extension.asFlowable
import dev.olog.msc.utils.media.store.notifySongMediaStore
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.withLatestFrom
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AlbumImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val lastFmGateway: LastFmGateway,
        private val imagesThreadPool: ImagesThreadPool
) {

    fun execute(list: List<Album>): Single<*> {
        return Single.fromCallable { list }
                .flattenAsFlowable { it }
                .onBackpressureBuffer()
                .subscribeOn(imagesThreadPool.ioScheduler)
                .filter { notExists(it) }
                .toList()
                .flatMap { fetchImages(it) }
    }

    private fun isConnected(): Flowable<Boolean> {
        return ReactiveNetwork.observeNetworkConnectivity(ctx)
                .map {
                    val isConnected = ConnectivityPredicate.hasState(NetworkInfo.State.CONNECTED).test(it)
                    val isWifi = ConnectivityPredicate.hasType(TYPE_WIFI, TYPE_ETHERNET).test(it)
                    val isMobile = ConnectivityPredicate.hasType(TYPE_MOBILE, TYPE_MOBILE_DUN).test(it)
                    isConnected && (isWifi || (ImagesCreator.CAN_DOWNLOAD_ON_MOBILE && isMobile))
                }
                .asFlowable()
    }

    private fun fetchImages(albums: List<Album>): Single<*>{

        return Flowables.zip(sample(albums.size), Flowable.fromIterable(albums),
                { _, album -> album })
                .onBackpressureBuffer()
                .observeOn(imagesThreadPool.ioScheduler)
                .withLatestFrom(isConnected(), { artist, isConnected ->
                    // check for every item if connection is still available, if not, throws an exception
                    if (!isConnected){
                        null
                    } else artist
                })
                .flatMapMaybe {
                    lastFmGateway.getAlbum(it.id, it.title, it.artist)
                            .filter { it.isPresent }
                            .map { it.get() }
                            .flatMap { lastFmGateway.insertAlbumImage(it.id, it.image).toMaybe<Boolean>() }
                            .onErrorComplete()
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

    /*
        Do not use interval below 500(2 times a second) due to lastFm
        limit to 5 request per second
     */
    private fun sample(times: Int): Flowable<*> {
        return Flowable.interval(500, TimeUnit.MILLISECONDS, imagesThreadPool.ioScheduler)
                .take(times.toLong())
    }

}