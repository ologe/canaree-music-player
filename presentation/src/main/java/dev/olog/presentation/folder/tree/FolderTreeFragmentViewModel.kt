package dev.olog.presentation.folder.tree

import android.content.Context
import android.database.CursorIndexOutOfBoundsException
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.FileType
import dev.olog.core.gateway.FolderNavigatorGateway
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.distinctUntilChanged
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FolderTreeFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val gateway: FolderNavigatorGateway

) : ViewModel() {

    private val currentDirectory = MutableStateFlow(appPreferencesUseCase.getDefaultMusicFolder())
    private val isCurrentFolderDefaultFolder = MutableLiveData<Boolean>()
    private val currentDirectoryChildrenLiveData = MutableLiveData<List<FolderTreeFragmentItem>>()

    init {
        viewModelScope.launch {
            currentDirectory
                .flatMapLatest { file ->
                    gateway.observeFolderChildren(file)
                        .map { addHeaders(file, it) }
                }
                .flowOn(Dispatchers.Default)
                .collect {
                    currentDirectoryChildrenLiveData.value = it
                }
        }
        viewModelScope.launch {
            currentDirectory.combine(appPreferencesUseCase.observeDefaultMusicFolder())
            { current, default -> current.path == default.path }
                .collect { isCurrentFolderDefaultFolder.value = it }
        }
    }

    private fun addHeaders(parent: File, files: List<FileType>): List<FolderTreeFragmentItem> {
        val folders = files.asSequence()
            .filterIsInstance(FileType.Folder::class.java)
            .map { it.toDisplayableItem() }
            .toList()
            .startWithIfNotEmpty(foldersHeader)

        val tracks = files.asSequence()
            .filterIsInstance(FileType.Track::class.java)
            .map { it.toDisplayableItem() }
            .toList()
            .startWithIfNotEmpty(tracksHeader)

        return buildList {
            if (parent != Environment.getRootDirectory()) {
                add(FolderTreeFragmentItem.Back)
            }
            addAll(folders)
            addAll(tracks)
        }
    }

    fun observeChildren(): LiveData<List<FolderTreeFragmentItem>> = currentDirectoryChildrenLiveData
    fun observeCurrentDirectoryFileName(): LiveData<File> = currentDirectory.asLiveData()
    fun observeCurrentFolderIsDefaultFolder(): LiveData<Boolean> = isCurrentFolderDefaultFolder.distinctUntilChanged()

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
    fun createMediaId(item: FolderTreeFragmentItem.Track): MediaId? {
        try {
            val file = File(item.path)
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

    private val foldersHeader = FolderTreeFragmentItem.Header(context.getString(R.string.common_folders))
    private val tracksHeader = FolderTreeFragmentItem.Header(context.getString(R.string.common_tracks))


    private fun FileType.Track.toDisplayableItem(): FolderTreeFragmentItem.Track {

        return FolderTreeFragmentItem.Track(
            mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, this.path),
            title = this.title,
            path = this.path
        )
    }

    private fun FileType.Folder.toDisplayableItem(): FolderTreeFragmentItem.Directory {
        return FolderTreeFragmentItem.Directory(
            mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, this.path),
            title = this.name,
            path = this.path
        )
    }
}