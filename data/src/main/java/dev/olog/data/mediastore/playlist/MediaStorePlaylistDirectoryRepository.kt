package dev.olog.data.mediastore.playlist

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import dev.olog.data.mediastore.MediaStoreQuery
import javax.inject.Inject

class MediaStorePlaylistDirectoryRepository @Inject constructor(
    private val mediaStorePlaylistDao: MediaStorePlaylistDao,
    private val queries: MediaStoreQuery,
) {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun get(): Uri? {
        val item = mediaStorePlaylistDao.getPlaylistDirectory()
        return item.documentUri?.let { Uri.parse(it) }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun set(documentUri: Uri?) {
        val path = queries.getDirectoryPath(documentUri)

        val item = MediaStorePlaylistDirectoryEntity(
            documentUri = documentUri?.toString(),
            path = path,
        )
        mediaStorePlaylistDao.replacePlaylistDirectory(item)
    }

}