package dev.olog.presentation.prefs.blacklist

import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.getMediaId
import dev.olog.core.gateway.FolderGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.extensions.mapToList
import io.reactivex.Observable
import javax.inject.Inject

class BlacklistFragmentPresenter @Inject constructor(
    folderGateway: FolderGateway,
    private val appPreferencesUseCase: BlacklistPreferences
) {

    val data: Observable<List<BlacklistModel>> = Observable.just(folderGateway.getAllBlacklistedIncluded())
        .mapToList { it.toDisplayableItem() }
        .map { folders ->
            val blacklisted = appPreferencesUseCase.getBlackList().map { it.toLowerCase() }
            folders.map {
                BlacklistModel(
                    it,
                    blacklisted.contains(it.subtitle!!.toLowerCase())
                )
            }
        }

    private fun Folder.toDisplayableItem(): DisplayableItem {
        return DisplayableItem(
            R.layout.dialog_blacklist_item,
            getMediaId(),
            this.title,
            this.path
        )
    }

    fun setDataSet(data: List<BlacklistModel>) {
        val blacklisted = data.filter { it.isBlacklisted }
            .mapNotNull { it.displayableItem.subtitle }
            .toSet()
        appPreferencesUseCase.setBlackList(blacklisted)
    }


}

class BlacklistModel(
    val displayableItem: DisplayableItem,
    var isBlacklisted: Boolean
)