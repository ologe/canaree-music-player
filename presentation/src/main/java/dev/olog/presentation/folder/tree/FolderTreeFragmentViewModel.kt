package dev.olog.presentation.folder.tree

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.entity.VirtualFileSystemNode
import dev.olog.core.entity.VirtualFileSystemTree
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.flow.*
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FolderTreeFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gateway: FolderGateway,
    private val prefs: AppPreferencesGateway,
) : ViewModel() {

    data class State(
        val currentNode: VirtualFileSystemNode,
        val fileSystemTree: VirtualFileSystemTree,
    ) {

        fun children(): List<VirtualFileSystemNode> {
            return fileSystemTree.findChildren(currentNode.path()).orEmpty()
        }

    }

    private val fileSystemState = MutableStateFlow<State?>(null)
    private val currentDirectoryChildrenLiveData = MutableLiveData<List<DisplayableItem>>()

    init {
        gateway.observeFileSystem()
            .onEach { vfs ->
                val initial = prefs.getLastFolderPath()?.let { vfs.findNode(it) } ?: vfs.root
                fileSystemState.value = State(initial, vfs)
            }.launchIn(viewModelScope)

        fileSystemState
            .filterNotNull()
            .flatMapLatest { state ->
                combine(
                    gateway.observeDirectories(state.children().map { it.path() }),
                    gateway.observeTrackListByPath(state.currentNode.path()),
                ) { directories, songs ->
                    addHeaders(directories, songs)
                }
            }.onEach {
                currentDirectoryChildrenLiveData.value = it
            }.launchIn(viewModelScope)
    }

    private fun addHeaders(originalDirectories: List<Folder>, originalSongs: List<Song>): List<DisplayableItem> {
        val directories = originalDirectories.asSequence()
            .map { it.toDisplayableItem() }
            .toList()
            .startWithIfNotEmpty(foldersHeader)

        val songs = originalSongs.asSequence()
            .map { it.toDisplayableItem() }
            .toList()
            .startWithIfNotEmpty(tracksHeader)

        return directories + songs
    }

    fun observeChildren(): LiveData<List<DisplayableItem>> = currentDirectoryChildrenLiveData
    fun observeCurrentDirectoryFileName(): LiveData<File> = fileSystemState
        .filterNotNull()
        .map { File(it.currentNode.path()) }
        .asLiveData()

    fun popFolder(): Boolean {
        val state = fileSystemState.value ?: return false
        val dirNode = state.currentNode
        if (dirNode.isRoot) {
            return false
        }

        val newNode = requireNotNull(dirNode.parent) { state }
        fileSystemState.value = State(
            currentNode = newNode,
            fileSystemTree = state.fileSystemTree
        )
        prefs.setLastFolderPath(newNode.path())
        return true
    }

    fun nextFolder(relativePath: String) {
        val state = fileSystemState.value ?: return
        val newNode = requireNotNull(state.fileSystemTree.findNode(relativePath)) { state }
        fileSystemState.value = State(
            currentNode = newNode,
            fileSystemTree = state.fileSystemTree
        )
        prefs.setLastFolderPath(newNode.path())
    }

    private val foldersHeader = DisplayableHeader(
        type = R.layout.item_folder_tree_header,
        mediaId = MediaId.headerId("folder header"),
        title = context.getString(R.string.common_folders),
        subtitle = null
    )

    private val tracksHeader = DisplayableHeader(
        type = R.layout.item_folder_tree_header,
        mediaId = MediaId.headerId("track header"),
        title = context.getString(R.string.common_tracks),
        subtitle = null
    )

    private fun Song.toDisplayableItem(): DisplayableTrack {
        return DisplayableTrack(
            type = R.layout.item_folder_tree_track,
            mediaId = getMediaId(),
            title = this.title,
            artist = this.artist, // TODO show something else?
            album = this.album, // TODO show something else?
            idInPlaylist = idInPlaylist,
        )
    }

    private fun Folder.toDisplayableItem(): DisplayableAlbum {
        return DisplayableAlbum(
            type = R.layout.item_folder_tree_directory,
            mediaId = getMediaId(),
            title = this.title,
            subtitle = this.path,
        )
    }

}