package dev.olog.data.repository

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.FileType
import dev.olog.core.gateway.FolderNavigatorGateway
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.prefs.BlacklistPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.io.File
import javax.inject.Inject

internal class FolderNavigatorRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val blacklistGateway: BlacklistPreferences,
    private val folderGateway: FolderGateway
) : FolderNavigatorGateway {

    override fun observeFolderChildren(file: File): Flow<List<FileType>> {
        return channelFlow {

            offer(queryFileChildren(file))

            val observer = ActionContentObserver { offer(queryFileChildren(file)) }

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
            .sortedBy { it.name.toLowerCase() }
            .map { FileType.Folder(it.name, it.path) }
            .toList()
    }

}

private class ActionContentObserver(
    private val action: () -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())) {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        action()
    }

}