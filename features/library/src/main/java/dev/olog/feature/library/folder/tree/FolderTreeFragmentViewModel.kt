package dev.olog.feature.library.folder.tree

import android.content.Context
import android.database.CursorIndexOutOfBoundsException
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.domain.entity.FileType
import dev.olog.domain.gateway.FolderNavigatorGateway
import dev.olog.domain.prefs.AppPreferencesGateway
import dev.olog.feature.library.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.File

class FolderTreeFragmentViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val gateway: FolderNavigatorGateway

) : ViewModel() {

    private val currentDirectory = MutableStateFlow(appPreferencesUseCase.getDefaultMusicFolder())

    private val isCurrentFolderDefaultFolder = MutableStateFlow(false)

    private val currentDirectoryChildrenPublisher = MutableStateFlow<List<FolderTreeFragmentModel>>(emptyList())

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

    private fun addHeaders(parent: File, files: List<FileType>): List<FolderTreeFragmentModel> {
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

    fun observeChildren(): Flow<List<FolderTreeFragmentModel>> = currentDirectoryChildrenPublisher
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
            Timber.e(e)
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
    fun createMediaId(item: FolderTreeFragmentModel.Track): MediaId? {
        try {
            val songPath = item.path
            val path = songPath.substring(0, songPath.lastIndexOf(File.separator))
            val folderMediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, path)

            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(BaseColumns._ID),
                "${MediaStore.Audio.AudioColumns.DATA} = ?",
                arrayOf(item.path), null
            )?.use { cursor ->
                cursor.moveToFirst()
                val trackId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                return MediaId.playableItem(folderMediaId, trackId)
            }
        } catch (ex: CursorIndexOutOfBoundsException) {
            Timber.e(ex)
        }
        return null
    }

    private val backDisplayableItem: FolderTreeFragmentModel = FolderTreeFragmentModel.Back

    private val foldersHeader = FolderTreeFragmentModel.Header(
        title = context.getString(R.string.common_folders),
    )

    private val tracksHeader = FolderTreeFragmentModel.Header(
        title = context.getString(R.string.common_tracks),
    )

    private fun FileType.Track.toDisplayableItem(): FolderTreeFragmentModel {

        return FolderTreeFragmentModel.Track(
            mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, this.path),
            title = this.title,
            path = this.path
        )
    }

    private fun FileType.Folder.toDisplayableItem(): FolderTreeFragmentModel {

        return FolderTreeFragmentModel.Directory(
            mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, this.path),
            title = this.name,
            path = this.path
        )
    }
}