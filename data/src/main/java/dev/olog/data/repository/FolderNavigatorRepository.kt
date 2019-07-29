package dev.olog.data.repository

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.FileType
import dev.olog.core.gateway.FolderNavigatorGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.data.utils.assertBackground
import dev.olog.data.utils.isAudioFile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.io.File
import javax.inject.Inject

internal class FolderNavigatorRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val blacklistGateway: BlacklistPreferences
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
        }.assertBackground()
    }

    private fun queryFileChildren(file: File): List<FileType> {
        val blacklisted = blacklistGateway.getBlackList()
        val children = file.listFiles()
            ?.filter { currentFile -> blacklisted.any { currentFile.path.startsWith(it) } }
            ?: return emptyList()

        val (directories, files) = children.partition { it.isDirectory }

        return sortFolders(directories) + sortTracks(files)
    }

    private fun sortFolders(files: List<File>): List<FileType> {
        return files.asSequence()
            .sortedBy { it.name }
            .map { FileType.Folder(it.name, it.path) }
            .toList()
    }

    private fun sortTracks(files: List<File>): List<FileType> {
        return files.asSequence()
            .filter { it.isAudioFile() }
            .sortedBy { it.name }
            .map { FileType.Track(it.name, it.path) }.toList()
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