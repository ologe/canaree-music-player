package dev.olog.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore.Audio.Media
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.FileType
import dev.olog.core.gateway.FolderNavigatorGateway
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.DataObserver
import kotlinx.coroutines.flow.*
import java.io.File
import javax.inject.Inject

internal class FolderNavigatorRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val schedulers: Schedulers,
    private val blacklistGateway: BlacklistPreferences,
    private val folderGateway: FolderGateway
) : FolderNavigatorGateway {

    private val contentResolver: ContentResolver
        get() = context.contentResolver

    override fun observeFolderChildren(file: File): Flow<List<FileType>> {
        val flow = MutableStateFlow<List<FileType>>(emptyList())

        val observer = DataObserver(schedulers.io) {
            flow.value = queryFileChildren(file)
        }
        contentResolver.registerContentObserver(Media.EXTERNAL_CONTENT_URI, true, observer)

        return flow
            .onStart { flow.value = queryFileChildren(file) }
            .onCompletion { contentResolver.unregisterContentObserver(observer) }
            .flowOn(schedulers.io)
    }

    private suspend fun queryFileChildren(file: File): List<FileType> {
        val blacklisted = blacklistGateway.getBlackList()
        val children = file.listFiles()
            ?.asSequence()
            ?.filter { it.isDirectory && !it.name.startsWith(".") }
            ?.filter { currentFile -> !blacklisted.any { currentFile.path.startsWith(it) } }
            ?.toList()
            ?: return emptyList()

        return sortFolders(children) + folderGateway
            .getTrackListByParam(file.path)
            .map { FileType.Track(it.title, it.path) }
    }

    private fun sortFolders(files: List<File>): List<FileType> {
        return files.asSequence()
            .sortedBy { it.name.toLowerCase() }
            .map { FileType.Folder(it.name, it.path) }
            .toList()
    }

}