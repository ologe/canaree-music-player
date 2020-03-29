@file:Suppress("DEPRECATION")

package dev.olog.presentation.folder.tree

import android.content.Context
import android.database.CursorIndexOutOfBoundsException import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.entity.FileType
import dev.olog.core.entity.track.Folder
import dev.olog.core.gateway.FolderNavigatorGateway
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.PresentationId
import dev.olog.presentation.PresentationIdCategory
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableFile
import dev.olog.presentation.widgets.BreadCrumbLayout
import dev.olog.shared.ApplicationContext
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class FolderTreeFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val gateway: FolderNavigatorGateway,
    schedulers: Schedulers

) : ViewModel() {

    var breadCrumbState: BreadCrumbLayout.SavedStateWrapper? = null

    private val currentDirectoryPublisher = ConflatedBroadcastChannel(appPreferencesUseCase.getDefaultMusicFolder())

    private val isCurrentFolderDefaultFolderPublisher = ConflatedBroadcastChannel<Boolean>()

    val canSaveDefaultFolder = currentDirectoryPublisher.asFlow()
        .map {
            it.absolutePath.length >= Environment.getExternalStorageDirectory().absolutePath.length
        }.combine(isCurrentFolderDefaultFolderPublisher.asFlow()) { canSave, isDefault ->
            canSave && !isDefault
        }

    val children: Flow<List<DisplayableFile>> = currentDirectoryPublisher.asFlow()
        .flatMapLatest { file ->
            gateway.observeFolderChildren(file)
                .map { addHeaders(it) }
        }.flowOn(schedulers.cpu)

    init {
        currentDirectoryPublisher.asFlow().combine(appPreferencesUseCase.observeDefaultMusicFolder())
        { current, default -> current.path == default.path }
            .onEach { isCurrentFolderDefaultFolderPublisher.offer(it) }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        currentDirectoryPublisher.close()
        isCurrentFolderDefaultFolderPublisher.close()
    }

    private fun addHeaders(files: List<FileType>): List<DisplayableFile> {
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

        return folders + tracks
    }

    val currentDirectoryFileName: Flow<File> = currentDirectoryPublisher.asFlow()

    fun popFolder(): Boolean {
        val current = currentDirectoryPublisher.value
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
            currentDirectoryPublisher.offer(current.parentFile!!)
            return true
        } catch (e: Exception) {
            Timber.e(e)
            return false
        }
    }

    fun nextFolder(file: File) {
        require(file.isDirectory)
        currentDirectoryPublisher.offer(file)
    }

    fun updateDefaultFolder() {
        val currentFolder = currentDirectoryPublisher.value
        require(currentFolder.isDirectory)
        appPreferencesUseCase.setDefaultMusicFolder(currentFolder)
    }

    @Suppress("DEPRECATION")
    fun createMediaId(item: DisplayableFile): PresentationId.Track? {
        try {
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(BaseColumns._ID),
                "${MediaStore.Audio.AudioColumns.DATA} = ?",
                arrayOf(item.path), null
            )?.use { cursor ->
                cursor.moveToFirst()
                val trackId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                return item.mediaId.playableItem(trackId)
            }
        } catch (ex: CursorIndexOutOfBoundsException) {
            Timber.e(ex)
        }
        return null
    }

    private val foldersHeader = DisplayableFile(
        R.layout.item_folder_tree_header,
        PresentationId.headerId("folder header"),
        context.getString(R.string.common_folders),
        null
    )

    private val tracksHeader = DisplayableFile(
        R.layout.item_folder_tree_header,
        PresentationId.headerId("track header"),
        context.getString(R.string.common_tracks),
        null
    )

    private fun FileType.Track.toDisplayableItem(): DisplayableFile {
        val mediaId = PresentationId.Category(
            PresentationIdCategory.FOLDERS,
            path
        )

        return DisplayableFile(
            type = R.layout.item_folder_tree_track,
            mediaId = mediaId,
            title = this.title,
            path = this.path
        )
    }

    private fun FileType.Folder.toDisplayableItem(): DisplayableFile {
        val mediaId = PresentationId.Category(
            PresentationIdCategory.FOLDERS,
            path
        )
        return DisplayableFile(
            type = R.layout.item_folder_tree_directory,
            mediaId = mediaId,
            title = this.name,
            path = this.path
        )
    }
}