package dev.olog.feature.edit.domain

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import dev.olog.lib.audio.tagger.AudioTagger
import dev.olog.lib.audio.tagger.model.Tags
import java.io.File
import javax.inject.Inject

class UpdateTrackUseCase @Inject constructor(
    private val context: Context,
    private val audioTagger: AudioTagger
) {

    operator fun invoke(param: Data) {
        audioTagger.save(param.file, param.tags)

        if (param.trackId != null) {
            updateMediaStore(param.trackId, param.isPodcast)
        }

        @Suppress("DEPRECATION")
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(param.file)
        context.sendBroadcast(intent)

    }

    private fun updateMediaStore(id: Long, isPodcast: Boolean?) {
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
        val values = ContentValues().apply {
            isPodcast?.let {
                put(MediaStore.Audio.Media.IS_PODCAST, it)
            }
            put(MediaStore.Audio.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        }
        context.contentResolver.update(uri, values, null, null)
    }

    data class Data(
        val trackId: Long?,
        val file: File,
        val tags: Tags,
        val isPodcast: Boolean
    )

}