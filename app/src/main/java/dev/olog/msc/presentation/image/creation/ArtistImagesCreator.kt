package dev.olog.msc.presentation.image.creation

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.tab.GetAllArtistsUseCase
import dev.olog.msc.domain.interactor.tab.GetAllSongsUseCase
import dev.olog.msc.utils.assertBackgroundThread
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.img.MergedImagesCreator
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

class ArtistImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val getAllSongsUseCase: GetAllSongsUseCase,
        private val lastFmService: LastFmService,
        private val getAllArtistsUseCase: GetAllArtistsUseCase

) {

    fun execute() : Single<*> {
//        return getAllSongsUseCase.execute()
//                .firstOrError()
//                .map { it.groupBy { it.artistId } }
//                .flattenAsObservable { it.entries }
//                .map { entry -> try {
//                    makeImage(entry)
//                } catch (ex: Exception){ false }
//                }
//                .reduce { acc: Boolean, curr: Boolean -> acc || curr }
//                .filter { it }
//                .doOnSuccess {
//                    ctx.contentResolver.notifyChange(MEDIA_STORE_URI, null)
//                }

        return getAllArtistsUseCase.execute()
                .firstOrError()
                .flattenAsObservable { it }
                .flatMap { artist -> Observable.timer(250, TimeUnit.MILLISECONDS).map { artist } }
                .map { lastFmService.fetchArtistArt(it.name) }
                .buffer(10)
                .doOnNext {
                    ctx.contentResolver.notifyChange(MEDIA_STORE_URI, null)
                }
                .toList()

    }

    private fun makeImage(map: Map.Entry<Long, List<Song>>) : Boolean {
        assertBackgroundThread()
        val folderName = ImagesFolderUtils.getFolderName(ImagesFolderUtils.ARTIST)
        return MergedImagesCreator.makeImages(ctx, map.value, folderName, "${map.key}")
    }

}