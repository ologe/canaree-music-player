package dev.olog.data.playable

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.contentresolversql.querySql
import dev.olog.core.entity.track.Song
import dev.olog.shared.android.extensions.toAndroidUri
import java.io.File
import java.net.URI
import javax.inject.Inject

internal class PlayableOperations @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun delete(playable: Song?): Long? {
        playable ?: return null

        val id = playable.id
        val path = playable.path
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
        val deleted = context.contentResolver.delete(uri, null, null)
        if (deleted < 1) {
            return null
        }

        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
        return id
    }

    // TODO can be improved?
    // TODO check if still works
    fun getByUri(uri: URI): Long? {
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
            SELECT ${MediaStore.Audio.Media._ID}
            FROM ${MediaStore.Audio.Media.EXTERNAL_CONTENT_URI}
            WHERE ${MediaStore.Audio.Media.DISPLAY_NAME} = ?
        """
            return context.contentResolver.querySql(itemQuery, arrayOf(displayName)).use {
                it.moveToFirst()
                it.getLongOrNull(0)
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return null
        }

    }

}