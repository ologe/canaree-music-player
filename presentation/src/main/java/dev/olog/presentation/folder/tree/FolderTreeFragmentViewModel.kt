package dev.olog.presentation.folder.tree

import android.content.Context
import android.database.CursorIndexOutOfBoundsException
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.FileType
import dev.olog.core.gateway.FolderNavigatorGateway
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.io.File

class FolderTreeFragmentViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val gateway: FolderNavigatorGateway

) : ViewModel() {

    companion object {
        val BACK_HEADER_ID = MediaId.headerId("back header")
    }

    private val currentDirectory = MutableStateFlow(appPreferencesUseCase.getDefaultMusicFolder())

    private val isCurrentFolderDefaultFolder = MutableStateFlow(false)

    private val currentDirectoryChildrenPublisher = MutableStateFlow<List<DisplayableFile>>(emptyList())

    init {
        currentDirectory
            .flatMapLatest { file ->
                gateway.observeFolderChildren(file)
                    .map { addHeaders(file, it) }
            }
            .flowOn(Dispatchers.Default)
            .onEach { currentDirectoryChildrenPublisher.value = it }
            .launchIn(viewModelScope)

        currentDirectory.combine(appPreferencesUseCase.observeDefaultMusicFolder())
        { current, default -> current.path == default.path }
            .onEach { isCurrentFolderDefaultFolder.value = it }
            .launchIn(viewModelScope)
    }

    private fun addHeaders(parent: File, files: List<FileType>): List<DisplayableFile> {
        val folders = files.asSequence()
            .filterIsInstance(FileType.Folder::class.java)
            .map { it.toDisplayableItem() }
            .toList()

        val tracks = files.asSequence()
            .filterIsInstance(FileType.Track::class.java)
            .map { it.toDisplayableItem() }
            .toList()

        return buildList {
            if (parent != Environment.getRootDirectory()) {
                add(backDisplayableItem)
            }

            if (folders.isNotEmpty()) {
                add(foldersHeader)
                addAll(folders)
            }
            if (tracks.isNotEmpty()) {
                add(tracksHeader)
                addAll(tracks)
            }
        }
    }

    fun observeChildren(): Flow<List<DisplayableFile>> = currentDirectoryChildrenPublisher
    fun observeCurrentDirectoryFileName(): Flow<File> = currentDirectory
    fun observeCurrentFolderIsDefaultFolder(): Flow<Boolean> = isCurrentFolderDefaultFolder

    fun popFolder(): Boolean {
        val current = currentDirectory.value
        if (current == Environment.getRootDirectory()) {
            // alredy in root dir
            return false
        }

        val parent = current.parentFile
        if (parent?.listFiles()?.isEmpty() == true) {
            // parent has not children
            return false
        }

        try {
            currentDirectory.value = current.parentFile!!
            return true
        } catch (e: Throwable) {
            e.printStackTrace()
            return false
        }
    }

    fun nextFolder(file: File) {
        require(file.isDirectory)
        currentDirectory.value = file
    }

    fun updateDefaultFolder() {
        val currentFolder = currentDirectory.value
        require(currentFolder.isDirectory)
        appPreferencesUseCase.setDefaultMusicFolder(currentFolder)
    }

    @Suppress("DEPRECATION")
    fun createMediaId(item: DisplayableFile): MediaId? {
        try {
            val file = item.asFile()
            val songPath = file.path
            val path = songPath.substring(0, songPath.lastIndexOf(File.separator))
            val folderMediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, path)

            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(BaseColumns._ID),
                "${MediaStore.Audio.AudioColumns.DATA} = ?",
                arrayOf(file.path), null
            )?.use { cursor ->
                cursor.moveToFirst()
                val trackId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                return MediaId.playableItem(folderMediaId, trackId)
            }
        } catch (ex: CursorIndexOutOfBoundsException) {
            ex.printStackTrace()
        }
        return null
    }

    private val backDisplayableItem: DisplayableFile = DisplayableFile(
        type = R.layout.item_folder_tree_directory,
        mediaId = BACK_HEADER_ID,
        title = "...",
        path = null
    )

    private val foldersHeader = DisplayableFile(
        type = R.layout.item_folder_tree_header,
        mediaId = MediaId.headerId("folder header"),
        title = context.getString(R.string.common_folders),
        path = null
    )

    private val tracksHeader = DisplayableFile(
        type = R.layout.item_folder_tree_header,
        mediaId = MediaId.headerId("track header"),
        title = context.getString(R.string.common_tracks),
        path = null
    )

    private fun FileType.Track.toDisplayableItem(): DisplayableFile {

        return DisplayableFile(
            type = R.layout.item_folder_tree_track,
            mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, this.path),
            title = this.title,
            path = this.path
        )
    }

    private fun FileType.Folder.toDisplayableItem(): DisplayableFile {

        return DisplayableFile(
            type = R.layout.item_folder_tree_directory,
            mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, this.path),
            title = this.name,
            path = this.path
        )
    }
}