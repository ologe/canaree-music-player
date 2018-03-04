package dev.olog.msc.presentation.image.creation

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Artist
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

class ArtistImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
//        private val lastFmService: LastFmClient, todo
        private val imagesThreadPool: ImagesThreadPool

) {

    fun execute(artists: List<Artist>) : Single<*> {
//        return Flowables.zip(sample(artists.size), Flowable.fromIterable(artists),
//                    { _, artist -> artist } )
//                .observeOn(imagesThreadPool.ioScheduler)
//                .flatMapSingle { lastFmService.fetchArtistArt(it.id, it.name).onErrorReturn { false } }
//                .buffer(10)
//                .map { it.reduce { acc, curr -> acc || curr } }
//                .filter { it }
//                .doOnNext {
//                    ctx.contentResolver.notifyChange(MEDIA_STORE_URI, null)
//                }.toList()
        return Single.just(false)
    }

    private fun sample(times: Int): Flowable<*>{
        return Flowable.interval(250, TimeUnit.MILLISECONDS, imagesThreadPool.ioScheduler)
                .take(times.toLong())
    }

}