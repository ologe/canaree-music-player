package dev.olog.msc.presentation.image.creation

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.utils.assertBackgroundThread
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.img.MergedImagesCreator
import io.reactivex.Maybe
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

class ArtistImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val songGateway: SongGateway

) {

    fun execute() : Maybe<*> {
        return songGateway.getAll()
                .firstOrError()
                .map { it.groupBy { it.artistId } }
                .flattenAsObservable { it.entries }
                .map { entry -> try {
                    makeImage(entry)
                } catch (ex: Exception){ false }
                }
                .reduce { acc: Boolean, curr: Boolean -> acc || curr }
                .filter { it }
                .doOnSuccess {
                    ctx.contentResolver.notifyChange(MEDIA_STORE_URI, null)
                }
    }

    private fun makeImage(map: Map.Entry<Long, List<Song>>) : Boolean {
        assertBackgroundThread()
        val folderName = ImagesFolderUtils.getFolderName(ImagesFolderUtils.ARTIST)
        return MergedImagesCreator.makeImages(ctx, map.value, folderName, "${map.key}")
    }

}