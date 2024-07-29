package dev.olog.presentation.prefs.blacklist

import android.os.Environment
import androidx.compose.runtime.Stable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Folder
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.prefs.BlacklistPreferences
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BlacklistFragmentViewModel @Inject constructor(
    folderGateway: FolderGateway,
    private val appPreferencesUseCase: BlacklistPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(
        kotlin.run {
            val blacklisted = appPreferencesUseCase.getBlackList().map { it.toLowerCase(Locale.getDefault()) }
            folderGateway.getAllBlacklistedIncluded().map { it.toDisplayableItem(blacklisted) }
        }
    )
    val liveData: LiveData<List<BlacklistItem>>
        get() = _state.asLiveData()

    val data: List<BlacklistItem>
        get() = _state.value

    fun updateToggleState(mediaId: MediaId, isBlacklisted: Boolean) {
        _state.value = _state.value.map {
            if (it.mediaId == mediaId) it.copy(isBlacklisted = isBlacklisted) else it
        }
    }

    private fun Folder.toDisplayableItem(blacklisted: List<String>): BlacklistItem {
        return BlacklistItem(
            mediaId = getMediaId(),
            title = this.title,
            path = this.path,
            isBlacklisted = blacklisted.contains(this.path.toLowerCase(Locale.getDefault()))
        )
    }

    fun saveBlacklisted(data: List<BlacklistItem>) {
        val blacklisted = data.filter { it.isBlacklisted }
            .map { it.path }
            .toSet()
        appPreferencesUseCase.setBlackList(blacklisted)
    }


}

@Stable
data class BlacklistItem(
    val mediaId: MediaId,
    val title: String,
    val path: String,
    val isBlacklisted: Boolean
) {

    companion object {
        @Suppress("DEPRECATION")
        @JvmStatic
        private val defaultStorageDir = Environment.getExternalStorageDirectory().path ?: "/storage/emulated/0/"
    }

    // show the path without "/storage/emulated/0"
    val displayablePath : String
        get() {
            return try {
                path.substring(defaultStorageDir.length)
            } catch (ex: StringIndexOutOfBoundsException){
                ex.printStackTrace()
                path
            }
        }

}