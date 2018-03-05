package dev.olog.msc.presentation.image.creation

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.gateway.LastFmGateway
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Flowables
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

class AlbumImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val lastFmGateway: LastFmGateway,
        private val imagesThreadPool: ImagesThreadPool
) {

    fun execute(albums: List<Album>): Single<*>{
        return Flowables.zip(sample(albums.size), Flowable.fromIterable(albums),
                { _, album -> album })
                .onBackpressureBuffer()
                .observeOn(imagesThreadPool.ioScheduler)
                .map { false } // todo if image not exist, fetch it
                .buffer(10)
                .map { it.reduce { acc, curr -> acc || curr } }
                .filter { it }
                .doOnNext {
                    ctx.contentResolver.notifyChange(MEDIA_STORE_URI, null)
                }
                .toList()
    }

    private fun sample(times: Int): Flowable<*> {
        return Flowable.interval(500, TimeUnit.MILLISECONDS, imagesThreadPool.ioScheduler)
                .take(times.toLong())
    }

}