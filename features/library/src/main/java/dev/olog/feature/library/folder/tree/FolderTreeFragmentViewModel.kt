@file:Suppress("DEPRECATION")

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
import dev.olog.domain.entity.FileType
import dev.olog.domain.gateway.FolderNavigatorGateway
import dev.olog.domain.prefs.AppPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationIdCategory
import dev.olog.feature.library.model.DisplayableFile
import dev.olog.feature.library.widgets.BreadCrumbLayout
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.File

internal class FolderTreeFragmentViewModel @ViewModelInject constructor(
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

    val children: Flow<List<DisplayableFile>> = TODO()

    init {
        currentDirectoryPublisher.asFlow().combine(appPreferencesUseCase.observeDefaultMusicFolder())
        { current, default -> current.path == default.path }
            .onEach { isCurrentFolderDefaultFolderPublisher.offer(it) }
            .launchIn(viewModelScope)
    }

    val currentDirectoryFileName: Flow<File> = currentDirectoryPublisher.asFlow()

    fun popFolder(): Boolean {
        val current = currentDirectoryPublisher.value
        if (current == Environment.getRootDirectory()) {
            // already in root dir
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
        TODO()
//        require(file.isDirectory)
//        currentDirectoryPublisher.offer(file)
    }

    fun updateDefaultFolder() {
        TODO()
//        val currentFolder = currentDirectoryPublisher.value
//        require(currentFolder.isDirectory)
//        appPreferencesUseCase.setDefaultMusicFolder(currentFolder)
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


}