package dev.olog.data.playable

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.contentresolversql.querySql
import dev.olog.core.MediaUri
import dev.olog.core.track.Song
import dev.olog.shared.android.extensions.toAndroidUri
import java.io.File
import java.net.URI
import javax.inject.Inject

internal class PlayableMediaStoreOperations @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun delete(playable: Song?): MediaUri? {
        val mediaStoreId = playable?.uri?.id?.toLongOrNull() ?: return null

        val path = playable.path
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaStoreId)
        val deleted = context.contentResolver.delete(uri, null, null)
        if (deleted < 1) {
            return null
        }

        // TODO check is is needed to delete also the file
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
        return playable.uri
    }

    // TODO can be improved?
    // TODO check if still works
    fun getByUri(uri: URI): MediaUri? {
        try {
            // https://developer.android.com/training/secure-file-sharing/retrieve-info
            // content uri has only two field [_id, _display_name]
            val fileQuery = """
            SELECT ${MediaStore.Audio.Media.DISPLAY_NAME}
            FROM ${uri.toAndroidUri()}
        """
            val displayName = context.contentResolver.querySql(fileQuery).use {
                it.moveToFirst()
                it.getStringOrNull(0)
            } ?: return null

            val itemQuery = """
            SELECT ${MediaStore.Audio.Media._ID}, ${MediaStore.Audio.Media.IS_PODCAST}
            FROM ${MediaStore.Audio.Media.EXTERNAL_CONTENT_URI}
            WHERE ${MediaStore.Audio.Media.DISPLAY_NAME} = ?
        """
            return context.contentResolver.querySql(itemQuery, arrayOf(displayName)).use {
                it.moveToFirst()
                val id = it.getString(0)
                val isPodcast = it.getInt(1) != 0
                MediaUri(MediaUri.Source.MediaStore, MediaUri.Category.Track, id, isPodcast)
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return null
        }

    }

}