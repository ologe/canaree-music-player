package dev.olog.msc.presentation.image.creation

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.repository.CommonQuery
import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.img.MergedImagesCreator
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI

class GenreImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val genreGateway: GenreGateway
) {


    fun execute() : Maybe<*> {
        return genreGateway.getAll()
                .firstOrError()
                .flattenAsFlowable { it }
                .parallel()
                .runOn(Schedulers.io())
                .map {
                    val uri = MediaStore.Audio.Genres.Members.getContentUri("external", it.id)
                    Pair(it, CommonQuery.extractAlbumIdsFromSongs(ctx.contentResolver, uri))
                }
                .map { (genre, albumsId) -> try {
                    runBlocking { makeImage(genre, albumsId).await() }
                } catch (ex: Exception){ false }
                }
                .sequential()
                .reduce { acc: Boolean, curr: Boolean -> acc || curr }
                .filter { it }
                .doOnSuccess {
                    ctx.contentResolver.notifyChange(MEDIA_STORE_URI, null)
                }
    }

    private fun makeImage(genre: Genre, albumsId: List<Long>) : Deferred<Boolean> = async {
        val folderName = ImagesFolderUtils.getFolderName(ImagesFolderUtils.GENRE)
        MergedImagesCreator.makeImages2(ctx, albumsId, folderName, "${genre.id}")
    }

}