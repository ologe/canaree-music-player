package dev.olog.data.repository

import android.content.Context
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.entity.FileType
import dev.olog.domain.gateway.FolderNavigatorGateway
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.schedulers.Schedulers
import dev.olog.data.DataObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.io.File
import java.util.*
import javax.inject.Inject

internal class FolderNavigatorRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val blacklistGateway: BlacklistPreferences,
    private val folderGateway: FolderGateway,
    private val schedulers: Schedulers
) : FolderNavigatorGateway {

    override fun observeFolderChildren(file: File): Flow<List<FileType>> {
        return channelFlow {

            offer(queryFileChildren(file))

            val observer = DataObserver(schedulers.io) { offer(queryFileChildren(file)) }

            context.contentResolver.registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                true,
                observer
            )

            awaitClose { context.contentResolver.unregisterContentObserver(observer) }
        }
    }

    private fun queryFileChildren(file: File): List<FileType> {
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
            .sortedBy { it.name.toLowerCase(Locale.ROOT) }
            .map { FileType.Folder(it.name, it.path) }
            .toList()
    }

}