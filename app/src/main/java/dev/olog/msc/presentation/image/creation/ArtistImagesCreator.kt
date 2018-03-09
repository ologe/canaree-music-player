package dev.olog.msc.presentation.image.creation

import android.content.Context
import android.net.ConnectivityManager
import android.provider.MediaStore
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.utils.k.extension.isSafeNetwork
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Flowables
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

class ArtistImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val connectivityManager: ConnectivityManager,
        private val lastFmGateway: LastFmGateway,
        private val imagesThreadPool: ImagesThreadPool

) {

    fun execute(pojo: ImageCreatorPojo<Artist>) : Single<*> {
        return Single.fromCallable { pojo.data }
                .flattenAsFlowable { it }
                .subscribeOn(imagesThreadPool.ioScheduler)
                .onBackpressureBuffer()
                .flatMapMaybe { artist -> lastFmGateway
                        .shouldFetchArtist(artist.id)
                        .filter { it }
                        .map { artist }
                        .onErrorComplete()
                }.toList()
                .flatMap { fetchImages(ImageCreatorPojo(pojo.canUseMobile, it)) }
    }

    private fun fetchImages(pojo: ImageCreatorPojo<Artist>): Single<*> {
        val (canUseMobile, artists) = pojo
        return Flowables.zip(sample(artists.size), Flowable.fromIterable(artists),
                { _, artist -> artist } )
                .onBackpressureBuffer()
                .observeOn(imagesThreadPool.ioScheduler)
                .filter { connectivityManager.isSafeNetwork(canUseMobile) }
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

    private fun sample(times: Int): Flowable<*>{
        return Flowable.interval(500, TimeUnit.MILLISECONDS, imagesThreadPool.ioScheduler)
                .take(times.toLong())
    }

}