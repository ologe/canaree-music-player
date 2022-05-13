package dev.olog.feature.settings.blacklist

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.entity.track.Folder
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.feature.settings.R
import dev.olog.shared.extension.lazyFast
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BlacklistFragmentViewModel @Inject constructor(
    folderGateway: FolderGateway,
    private val appPreferencesUseCase: BlacklistPreferences
) : ViewModel() {

    val data : List<BlacklistModel> by lazyFast {
        val blacklisted = appPreferencesUseCase.getBlackList().map { it.toLowerCase(Locale.getDefault()) }
        folderGateway.getAllBlacklistedIncluded().map { it.toDisplayableItem(blacklisted) }
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