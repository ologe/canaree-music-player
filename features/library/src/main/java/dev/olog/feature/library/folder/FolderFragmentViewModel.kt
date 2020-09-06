package dev.olog.feature.library.folder

import android.content.Context
import android.content.res.Resources
import android.os.Environment
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.entity.FileType
import dev.olog.domain.entity.track.Folder
import dev.olog.domain.gateway.FolderNavigatorGateway
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.domain.prefs.AppPreferencesGateway
import dev.olog.feature.library.R
import dev.olog.feature.library.model.TabCategory
import dev.olog.feature.library.prefs.LibraryPreferences
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationIdCategory
import dev.olog.feature.presentation.base.model.presentationId
import dev.olog.shared.coroutines.mapListItem
import dev.olog.shared.startWith
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.File

internal class FolderFragmentViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val gateway: FolderGateway,
    private val navigatorGateway: FolderNavigatorGateway,
    private val prefs: AppPreferencesGateway,
    private val libraryPrefs: LibraryPreferences
) : ViewModel() {

    private val currentFolderFlow = MutableStateFlow(prefs.getDefaultMusicFolder())

    val isHierarchyFlow: Flow<Boolean>
//        get() = libraryPrefs.observeFolderHierarchy()
        get() = emptyFlow()

    val isHierarchyEnabled: Boolean = false
//        get() = libraryPrefs.getFolderHierarchy()

    val data: Flow<List<FolderFragmentItem>>
        get() = isHierarchyFlow
            .flatMapLatest { isHierarchy ->
                if (isHierarchy) {
                    hierarchyData()
                } else {
                    albumData()
                }
            }

    private fun hierarchyData(): Flow<List<FolderFragmentItem>> {
        return currentFolderFlow.flatMapLatest { file ->
            navigatorGateway.observeFolderChildren(file).map { convertData(it, file) }
        }
    }

    private fun convertData(files: List<FileType>, current: File): List<FolderFragmentItem> {
        val folders = files.asSequence()
            .filterIsInstance(FileType.Folder::class.java)
            .map { it.toDisplayableItem() }
            .toList()
            .startWithIfNotEmpty(FolderFragmentItem.Header(context.getString(R.string.common_folders)))

        val tracks = files.asSequence()
            .filterIsInstance(FileType.Track::class.java)
            .map { it.toDisplayableItem() }
            .toList()
            .startWithIfNotEmpty(FolderFragmentItem.Header(context.getString(R.string.common_tracks)))

        return (folders + tracks).startWith(FolderFragmentItem.BreadCrumb(current))
    }

    private fun albumData(): Flow<List<FolderFragmentItem>> {
        return gateway.observeAll()
            .mapListItem { it.toAlbum(context.resources) }
    }

    // TODO remove TabCategory
    fun getSpanCount() = libraryPrefs.getSpanCount(TabCategory.FOLDERS)

    fun setIsHierarchy(enabled: Boolean) {
        if (enabled) {
            currentFolderFlow.value = prefs.getDefaultMusicFolder()
        }
//        libraryPrefs.setFolderHierarchy(enabled)
    }

    fun updateFolder(file: File) {
        currentFolderFlow.value = file
    }

    fun updateFolder(item: FolderFragmentItem.Folder) {
        updateFolder(File(item.path))
    }

    fun popFolder(): Boolean {
        val current = currentFolderFlow.value
        if (current == Environment.getRootDirectory()) {
            // already in root dir
            return false
        }
        val parent = current.parentFile ?: return false

        if (parent.listFiles()?.isEmpty() == true) {
            // parent has not children
            return false
        }

        currentFolderFlow.value = parent
        return true
    }

    fun updateDefaultFolder() {
        prefs.setDefaultMusicFolder(currentFolderFlow.value)
    }

    private fun Folder.toAlbum(
        resources: Resources
    ): FolderFragmentItem.Album {
        return FolderFragmentItem.Album(
            mediaId = presentationId,
            title = title,
            subtitle = DisplayableAlbum.readableSongCount(resources, size)
        )
    }

    private fun FileType.Track.toDisplayableItem(): FolderFragmentItem.File {
        val mediaId = PresentationId.Category(PresentationIdCategory.FOLDERS, path)

        return FolderFragmentItem.File(
            mediaId = mediaId,
            title = this.title,
            path = this.path
        )
    }

    private fun FileType.Folder.toDisplayableItem(): FolderFragmentItem.Folder {
        val mediaId = PresentationId.Category(PresentationIdCategory.FOLDERS, path)
        return FolderFragmentItem.Folder(
            mediaId = mediaId,
            title = this.name,
            path = this.path
        )
    }

}