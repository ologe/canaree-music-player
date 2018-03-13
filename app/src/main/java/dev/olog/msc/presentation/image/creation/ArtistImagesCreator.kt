package dev.olog.msc.presentation.image.creation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.MediaStore
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.utils.k.extension.asFlowable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.withLatestFrom
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

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

    private fun isConnected(): Flowable<Boolean> {
        return ReactiveNetwork.observeNetworkConnectivity(ctx)
                .map {
                    val isConnected = ConnectivityPredicate.hasState(NetworkInfo.State.CONNECTED).test(it)
                    val isWifi = ConnectivityPredicate.hasType(ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_ETHERNET).test(it)
                    val isMobile = ConnectivityPredicate.hasType(ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_MOBILE_DUN).test(it)
                    isConnected && (isWifi || (ImagesCreator.CAN_DOWNLOAD_ON_MOBILE && isMobile))
                }
                .asFlowable()
    }

    private fun fetchImages(artists: List<Artist>): Single<*> {
        return Flowables.zip(sample(artists.size), Flowable.fromIterable(artists),
                { _, artist -> artist } )
                .onBackpressureBuffer()
                .observeOn(imagesThreadPool.ioScheduler)
                .withLatestFrom(isConnected(), { artist, isConnected ->
                    // check for every item if connection is still available, if not, throws an exception
                    if (!isConnected){
                        null
                    } else artist
                })
                .flatMapSingle {
                    lastFmGateway.getArtist(it.id, it.name).onErrorReturnItem(false)
                }
                .buffer(10)
                .map { it.reduce { acc, curr -> acc || curr } }
                .filter { it }
                .doOnNext {
                    ctx.contentResolver.notifyChange(MEDIA_STORE_URI, null)
                }.toList()
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