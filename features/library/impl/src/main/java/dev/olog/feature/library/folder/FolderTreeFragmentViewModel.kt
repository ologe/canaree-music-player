package dev.olog.feature.library.folder

import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.entity.FolderContent
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.prefs.AppPreferencesGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import dev.olog.feature.library.R
import dev.olog.ui.model.DisplayableAlbum
import dev.olog.ui.model.DisplayableHeader
import dev.olog.ui.model.DisplayableItem
import dev.olog.ui.model.DisplayableTrack

@HiltViewModel
class FolderTreeFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val gateway: FolderGateway,
) : ViewModel() {

    private val currentDirectory = MutableStateFlow<File?>(null)
    private val isCurrentFolderDefaultFolder = MutableStateFlow(false)
    private val currentFolderContent = MutableStateFlow<FolderContent?>(null)

    init {
        viewModelScope.launch {
            val folders = gateway.getAll()
            val defaultMusicFolder = appPreferencesUseCase.getDefaultMusicFolder()
            if (folders.any { it.path == defaultMusicFolder.path }) {
                // exists
                currentDirectory.value = defaultMusicFolder
            } else {
                // not exists anymore, find the broadest one
                currentDirectory.value = folders
                    .minByOrNull { folder -> folder.path.count { it == File.separatorChar } }
                    ?.let { File(it.path) }
                    ?.also { appPreferencesUseCase.setDefaultMusicFolder(it) }
            }
        }

        currentDirectory
            .filterNotNull()
            .mapLatest { gateway.getFolderContent(it.path) }
            .onEach { currentFolderContent.value = it }
            .launchIn(viewModelScope)

        currentDirectory.combine(appPreferencesUseCase.observeDefaultMusicFolder()) { a, b -> a?.path == b.path }
            .onEach { isCurrentFolderDefaultFolder.value = it }
            .launchIn(viewModelScope)
    }

    fun observeChildren(): Flow<List<DisplayableItem>> = currentFolderContent.filterNotNull()
        .map {
            buildList {
                val subFolders = it.subFolders
                val songs = it.songs
                if (subFolders.isNotEmpty()) {
                    add(
                        DisplayableHeader(
                            R.layout.item_folder_tree_header,
                            MediaId.headerId("folders header"),
                            context.getString(localization.R.string.common_folders),
                        )
                    )
                    this += subFolders.map {
                        DisplayableAlbum(
                            type = R.layout.item_folder_tree_directory,
                            mediaId = it.getMediaId(),
                            title = it.title,
                            subtitle = it.path,
                        )
                    }
                }
                if (songs.isNotEmpty()) {
                    add(
                        DisplayableHeader(
                            R.layout.item_folder_tree_header,
                            MediaId.headerId("songs header"),
                            context.getString(localization.R.string.common_tracks),
                        )
                    )
                    this += songs.map {
                        DisplayableTrack(
                            type = R.layout.item_folder_tree_track,
                            mediaId = it.getMediaId(),
                            title = it.title,
                            artist = it.artist,
                            album = it.album,
                            idInPlaylist = -1,
                            dataModified = -1,
                        )
                    }
                }
            }
        }
    fun observeCurrentDirectoryFileName(): Flow<File> = currentDirectory.filterNotNull()
    fun observeCurrentFolderIsDefaultFolder(): Flow<Boolean> = isCurrentFolderDefaultFolder

    fun popFolder(): Boolean {
        val current = currentDirectory.value
        if (current == Environment.getRootDirectory()) {
//             alredy in root dir
            return false
        }

        val parent = current?.parentFile ?: return false
        if (parent.listFiles()?.isEmpty() == true) {
//             parent has not children
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
        val currentFolder = currentDirectory.value ?: return
        require(currentFolder.isDirectory)
        appPreferencesUseCase.setDefaultMusicFolder(currentFolder)
    }

}