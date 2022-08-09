package dev.olog.data.mediastore

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.contentresolversql.querySql
import dev.olog.core.entity.track.Song
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.utils.getString
import java.io.File
import javax.inject.Inject

class MediaStoreUtils @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaStoreAudioDao: MediaStoreAudioDao,
    private val schedulers: Schedulers,
) {

    suspend fun delete(id: String, findPathById: (id: String) -> String?) {
        deleteInternal(id, findPathById)
    }

    suspend fun deleteGroup(ids: List<String>, findPathById: (id: String) -> String?) {
        ids.forEach { deleteInternal(it, findPathById) }
    }

    private suspend fun deleteInternal(
        id: String,
        findPathById: (id: String) -> String?
    ) = with(schedulers.io) {
        val path = findPathById(id) ?: return
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toLong())
        val deleted = context.contentResolver.delete(uri, null, null)
        if (deleted < 1) {
            Log.e("SongRepo", "song not found $id")
            return
        }

        val file = File(path)
        if (file.exists()) {
            file.delete()
        }

        mediaStoreAudioDao.delete(id)
    }

    fun getByUri(
        uri: Uri,
        findByDisplayName: (displayName: String) -> Song?
    ): Song? {
        // https://developer.android.com/training/secure-file-sharing/retrieve-info
        // content uri has only two field [_id, _display_name]
        val fileQuery = """
            SELECT ${MediaStore.Audio.Media.DISPLAY_NAME}
            FROM $uri
        """
        val displayName = context.contentResolver.querySql(fileQuery).use {
            it.moveToFirst()
            it.getString(MediaStore.Audio.Media.DISPLAY_NAME)
        }
        return findByDisplayName(displayName)
    }

}