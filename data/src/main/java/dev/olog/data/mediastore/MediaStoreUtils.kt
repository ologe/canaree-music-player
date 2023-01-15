package dev.olog.data.mediastore

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.database.getStringOrNull
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.contentresolversql.querySql
import dev.olog.core.entity.track.Song
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class MediaStoreUtils @Inject constructor(
    @ApplicationContext private val context: Context,
    private val schedulers: Schedulers,
) {

    suspend fun deleteSingle(
        id: Long,
        findPathById: suspend (id: Long) -> String?
    ) {
        return deleteInternal(id, findPathById)
    }

    suspend fun deleteGroup(
        ids: List<Song>,
        findPathById: suspend (id: Long) -> String?
    ) {
        for (id in ids) {
            deleteInternal(id.id, findPathById)
        }
    }

    private suspend fun deleteInternal(
        id: Long,
        findPathById: suspend (id: Long) -> String?
    ) = withContext(schedulers.io) {
        // TODO check if works on android 13
        val path = findPathById(id) ?: return@withContext
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
        val deleted = context.contentResolver.delete(uri, null, null)
        if (deleted < 1) {
            Log.w("SongRepo", "song not found $id")
            return@withContext
        }

        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    suspend fun getByUri(
        uri: Uri,
        findByDisplayName: suspend (displayName: String) -> Song?
    ): Song? = withContext(schedulers.io) {
        // https://developer.android.com/training/secure-file-sharing/retrieve-info
        // content uri has only two field [_id, _display_name]
        val fileQuery = """
            SELECT ${MediaStore.Audio.Media.DISPLAY_NAME}
            FROM $uri
        """
        val displayName = context.contentResolver.querySql(fileQuery).use {
            it.moveToFirst()
            it.getStringOrNull(it.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
        } ?: return@withContext null

        return@withContext findByDisplayName(displayName)
    }

}