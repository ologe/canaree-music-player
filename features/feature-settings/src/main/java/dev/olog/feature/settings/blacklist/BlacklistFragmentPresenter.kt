package dev.olog.feature.settings.blacklist

import dev.olog.domain.entity.track.Folder
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.feature.presentation.base.model.presentationId
import dev.olog.feature.settings.R
import dev.olog.feature.settings.model.BlacklistModel
import dev.olog.shared.lazyFast
import java.util.*
import javax.inject.Inject

internal class BlacklistFragmentPresenter @Inject constructor(
    folderGateway: FolderGateway,
    private val appPreferencesUseCase: BlacklistPreferences
) {

    val data : List<BlacklistModel> by lazyFast {
        val blacklisted = appPreferencesUseCase.getBlackList().map { it.toLowerCase(Locale.getDefault()) }
        folderGateway.getAllBlacklistedIncluded().map { it.toDisplayableItem(blacklisted) }
    }

    private fun Folder.toDisplayableItem(blacklisted: List<String>): BlacklistModel {
        return BlacklistModel(
            R.layout.dialog_blacklist_item,
            presentationId,
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

