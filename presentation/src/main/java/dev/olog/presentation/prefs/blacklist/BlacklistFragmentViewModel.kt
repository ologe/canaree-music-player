package dev.olog.presentation.prefs.blacklist

import android.os.Environment
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Folder
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.presentation.R
import dev.olog.presentation.model.BaseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*

internal class BlacklistFragmentViewModel @ViewModelInject constructor(
    folderGateway: FolderGateway,
    private val appPreferencesUseCase: BlacklistPreferences
): ViewModel() {

    private val _data = MutableStateFlow<List<BlacklistModel>>(emptyList())
    val data: Flow<List<BlacklistModel>>
        get() = _data

    init {
        viewModelScope.launch {
            val blacklisted = appPreferencesUseCase.getBlackList()
                .map { it.toLowerCase(Locale.getDefault()) }
            val result = folderGateway.getAllBlacklistedIncluded()
                .map { it.toDisplayableItem(blacklisted) }

            _data.value = result
        }
    }

    private fun Folder.toDisplayableItem(blacklisted: List<String>): BlacklistModel {
        return BlacklistModel(
            R.layout.dialog_blacklist_item,
            getMediaId(),
            this.title,
            this.path,
            blacklisted.contains(this.path.toLowerCase(Locale.getDefault()))
        )
    }

    fun saveBlacklisted(data: List<BlacklistModel>) {
        val blacklisted = data.filter { it.isBlacklisted }
            .map { it.path }
            .toSet()
        appPreferencesUseCase.setBlackList(blacklisted)
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