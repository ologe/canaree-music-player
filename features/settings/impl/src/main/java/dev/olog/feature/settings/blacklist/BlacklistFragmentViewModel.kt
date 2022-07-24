package dev.olog.feature.settings.blacklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.entity.track.Folder
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.feature.settings.R
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BlacklistFragmentViewModel @Inject constructor(
    private val folderGateway: FolderGateway,
    private val appPreferencesUseCase: BlacklistPreferences
) : ViewModel() {

    suspend fun data(): List<BlacklistModel> {
        val blacklisted = appPreferencesUseCase.getBlackList().map { it.toLowerCase(Locale.getDefault()) }
        return folderGateway.getAllBlacklistedIncluded().map { it.toDisplayableItem(blacklisted) }
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
        viewModelScope.launch {
            val blacklisted = data.filter { it.isBlacklisted }
                .map { it.path }
            appPreferencesUseCase.setBlackList(blacklisted)
        }
    }


}