package dev.olog.data.repository

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FolderGateway2
import dev.olog.core.gateway.Path
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.queries.FolderQueries
import dev.olog.data.utils.getString
import dev.olog.data.utils.queryAll
import dev.olog.shared.assertBackground
import dev.olog.shared.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

internal class FolderRepository2 @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences
)  : BaseRepository<Folder, Path>(context), FolderGateway2 {

    private val queries = FolderQueries(contentResolver, blacklistPrefs, sortPrefs)

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    private fun extractFolders(cursor: Cursor): List<Folder> {
        assertBackgroundThread()
        val pathList = context.contentResolver.queryAll(cursor) {
            val data = it.getString(MediaStore.Audio.AudioColumns.DATA)
            data.substring(1, data.lastIndexOf(File.separator)) // path
        }
        return pathList.asSequence()
            .groupBy { it }
            .entries
            .map { (path, list) ->
                val dirName = path.substring(path.lastIndexOf(File.separator) + 1)
                Folder(
                    dirName.capitalize(),
                    path,
                    list.size
                )
            }.sortedBy { it.title }
    }

    override fun queryAll(): List<Folder> {
        assertBackgroundThread()
        val cursor = queries.getAll(false)
        return extractFolders(cursor)
    }

    override fun getByParam(param: Path): Folder? {
        assertBackgroundThread()
        return channel.valueOrNull?.find { it.path == param }
    }

    override fun observeByParam(param: Path): Flow<Folder?> {
        return channel.asFlow().map { list -> list.find { it.path == param } }
            .assertBackground()
    }

    override fun getTrackListByParam(param: Path): List<Song> {
        return listOf()
    }

    override fun observeTrackListByParam(param: Path): Flow<List<Song>> {
        return flow {  }
    }
}