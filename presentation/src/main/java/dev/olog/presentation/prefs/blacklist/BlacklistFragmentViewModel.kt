package dev.olog.presentation.prefs.blacklist

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Folder
import dev.olog.core.gateway.BlacklistGateway
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.R
import dev.olog.presentation.model.BaseModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

// TODO rewrite
@HiltViewModel
class BlacklistFragmentViewModel @Inject constructor(
    folderGateway: FolderGateway,
    private val blacklistGateway: BlacklistGateway,
    schedulers: Schedulers,
) : ViewModel() {

    val data: LiveData<List<BlacklistModel>> = liveData(schedulers.io) {
        val blacklisted = blacklistGateway.getAll().map { it.lowercase() }
        val data = folderGateway.getAllBlacklistedIncluded().map { it.toDisplayableItem(blacklisted) }
        emit(data)
    }

    // TODO ensure path is relative path and not full path
    private fun Folder.toDisplayableItem(blacklisted: List<String>): BlacklistModel {
        return BlacklistModel(
            type = R.layout.dialog_blacklist_item,
            mediaId = getMediaId(),
            title = this.title,
            path = this.path,
            isBlacklisted = blacklisted.contains(this.path.lowercase())
        )
    }

    fun saveBlacklisted(data: List<BlacklistModel>) {
        viewModelScope.launch {
            val blacklisted = data.filter { it.isBlacklisted }.map { it.path }
            blacklistGateway.setAll(blacklisted)
        }
    }


}

data class BlacklistModel(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val path: String,
    var isBlacklisted: Boolean
) : BaseModel {

    companion object {
        @Suppress("DEPRECATION")
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