package dev.olog.feature.library.folder

import android.content.Context
import android.content.res.Resources
import android.os.Environment
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.MediaIdCategory
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
import dev.olog.navigation.Navigator
import dev.olog.shared.coroutines.mapListItem
import dev.olog.shared.startWith
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.flow.*
import java.io.File

internal class FolderFragmentViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val gateway: FolderGateway,
    private val navigatorGateway: FolderNavigatorGateway,
    private val prefs: AppPreferencesGateway,
    private val preferences: LibraryPreferences,
//    private val navigator: Navigator
) : ViewModel() {

    private val currentFolderFlow = MutableStateFlow(prefs.getDefaultMusicFolder())
    val currentFolder: Flow<File>
        get() = currentFolderFlow

    val isFolderHierarchyFlow: Flow<Boolean>
        get() = preferences.isFolderHierarchyFlow

    val data: Flow<List<FolderFragmentModel>>
        get() = isFolderHierarchyFlow
            .flatMapLatest { isHierarchy ->
                if (isHierarchy) {
                    hierarchyData()
                } else {
                    albumData()
                }
            }

    private fun hierarchyData(): Flow<List<FolderFragmentModel>> {
        return currentFolderFlow.flatMapLatest { file ->
            navigatorGateway.observeFolderChildren(file).map { convertData(it) }
        }
    }

    private fun convertData(files: List<FileType>): List<FolderFragmentModel> {
        val folders = files.asSequence()
            .filterIsInstance(FileType.Folder::class.java)
            .map { it.toDisplayableItem() }
            .toList()
            .startWithIfNotEmpty(FolderFragmentModel.Header(context.getString(R.string.common_folders)))

        val tracks = files.asSequence()
            .filterIsInstance(FileType.Track::class.java)
            .map { it.toDisplayableItem() }
            .toList()
            .startWithIfNotEmpty(FolderFragmentModel.Header(context.getString(R.string.common_tracks)))

        return folders + tracks
    }

    private fun albumData(): Flow<List<FolderFragmentModel>> {
        return gateway.observeAll()
            .mapListItem { it.toAlbum(context.resources) }
    }

    fun updateFolder(file: File) {
        currentFolderFlow.value = file
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
    ): FolderFragmentModel.Album {
        return FolderFragmentModel.Album(
            mediaId = presentationId,
            title = title,
            subtitle = DisplayableAlbum.readableSongCount(resources, size)
        )
    }

    private fun FileType.Track.toDisplayableItem(): FolderFragmentModel.File {
        val mediaId = PresentationId.Category(PresentationIdCategory.FOLDERS, path)

        return FolderFragmentModel.File(
            mediaId = mediaId,
            title = this.title,
            path = this.path
        )
    }

    private fun FileType.Folder.toDisplayableItem(): FolderFragmentModel.Folder {
        val mediaId = PresentationId.Category(PresentationIdCategory.FOLDERS, path)
        return FolderFragmentModel.Folder(
            mediaId = mediaId,
            title = this.name,
            path = this.path
        )
    }

    fun observeSpanCount() = preferences
        .observeSpanCount(MediaIdCategory.FOLDERS)

    fun setIsFolderHierarchy() {
        val newState = !preferences.isFolderHierarchy
        if (newState) {
            currentFolderFlow.value = prefs.getDefaultMusicFolder()
        }
        preferences.isFolderHierarchy = newState
    }

    fun updateSpan() {
//        navigator.toLibrarySpan(MediaIdCategory.FOLDERS)
    }

}