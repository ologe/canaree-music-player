package dev.olog.feature.library.folder.tree

import android.content.Context
import android.database.CursorIndexOutOfBoundsException
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaUri
import dev.olog.core.folder.FileType
import dev.olog.core.folder.FolderNavigatorGateway
import dev.olog.core.prefs.AppPreferencesGateway
import kotlinx.coroutines.flow.*
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FolderTreeFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val gateway: FolderNavigatorGateway,
) : ViewModel() {

    private val currentDirectory = MutableStateFlow(appPreferencesUseCase.defaultMusicFolder.get())

    private val isCurrentFolderDefaultFolder = MutableStateFlow<Boolean?>(null)
    fun observeCurrentFolderIsDefaultFolder(): Flow<Boolean> = isCurrentFolderDefaultFolder.filterNotNull()

    private val _data = MutableStateFlow<Pair<List<FileType.Folder>, List<FileType.Track>>?>(null)
    fun data(): Flow<Pair<List<FileType.Folder>, List<FileType.Track>>> = _data.filterNotNull()

    fun observeCurrentDirectoryFileName(): Flow<File> = appPreferencesUseCase.defaultMusicFolder.observe()

    init {
        currentDirectory
            .flatMapLatest { gateway.observeFolderChildren(it) }
            .onEach {
                val folder = it.filterIsInstance<FileType.Folder>()
                val tracks = it.filterIsInstance<FileType.Track>()
                _data.value = folder to tracks
            }
            .launchIn(viewModelScope)

        currentDirectory
            .combine(appPreferencesUseCase.defaultMusicFolder.observe()) { current, default ->
                current.path == default.path
            }.onEach { isCurrentFolderDefaultFolder.value = it }
            .launchIn(viewModelScope)
    }

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
        appPreferencesUseCase.defaultMusicFolder.set(currentFolder)
    }

    @Suppress("DEPRECATION")
    fun createMediaId(item: FileType): MediaUri? {
        try {
            val file = item.toFile()

            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(BaseColumns._ID, MediaStore.Audio.Media.IS_PODCAST),
                "${MediaStore.Audio.AudioColumns.DATA} = ?",
                arrayOf(file.path), null
            )?.use { cursor ->
                cursor.moveToFirst()
                val trackId = cursor.getString(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                val isPodcast = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_PODCAST)) != 0L
                return MediaUri(MediaUri.Source.MediaStore, MediaUri.Category.Track, trackId, isPodcast)
            }
        } catch (ex: CursorIndexOutOfBoundsException) {
            ex.printStackTrace()
        }
        return null
    }

}