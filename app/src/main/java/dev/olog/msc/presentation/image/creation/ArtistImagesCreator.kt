package dev.olog.msc.presentation.image.creation

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.api.last.fm.LastFmClient
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.interactor.tab.GetAllArtistsUseCase
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

class ArtistImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val lastFmService: LastFmClient,
        private val getAllArtistsUseCase: GetAllArtistsUseCase

) {

    fun execute() : Single<*> {

        return Observables.zip(
                Observable.interval(200, TimeUnit.MILLISECONDS),
                getAllArtistsUseCase.execute().firstOrError().flattenAsObservable { it },
                { _, artist -> artist } )
                .flatMapSingle { lastFmService.fetchArtistArt(it.id, it.name) }
                .buffer(10)
                .map { it.reduce { acc, curr -> acc && curr } }
                .filter { it }
                .doOnNext {
                    ctx.contentResolver.notifyChange(MEDIA_STORE_URI, null)
                }.toList()

    }

}