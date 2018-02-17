package dev.olog.msc.presentation.image.creation

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.img.MergedImagesCreator
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.io.File
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

class FolderImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val songGateway: SongGateway

) {

    fun execute() : Maybe<*> {
        return songGateway.getAll()
                .firstOrError()
                .map { it.groupBy { it.folderPath } }
                .flattenAsFlowable { it.entries }
                .parallel()
                .runOn(Schedulers.io())
                .map { entry -> try {
                    runBlocking { makeImage(entry).await() }
                } catch (ex: Exception){ false }
                }.sequential()
                .reduce { acc: Boolean, curr: Boolean -> acc || curr }
                .filter { it }
                .doOnSuccess {
                    ctx.contentResolver.notifyChange(MEDIA_STORE_URI, null)
                }
    }


    private fun makeImage(map: Map.Entry<String, List<Song>>) : Deferred<Boolean> = async {
        val folderName = ImagesFolderUtils.getFolderName(ImagesFolderUtils.FOLDER)
        val normalizedPath = map.key.replace(File.separator, "")
        MergedImagesCreator.makeImages(ctx, map.value, folderName, normalizedPath)
    }

}