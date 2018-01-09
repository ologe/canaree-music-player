package dev.olog.presentation.activity_preferences.blacklist

import dev.olog.domain.entity.Folder
import dev.olog.domain.interactor.prefs.BlackListUseCase
import dev.olog.domain.interactor.prefs.GetAllFoldersUnfiltered
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaId
import dev.olog.shared.groupMap
import javax.inject.Inject

class BlacklistFragmentPresenter @Inject constructor(
        getAllFoldersUnfiltered: GetAllFoldersUnfiltered,
        private val blackListUseCase: BlackListUseCase
) {

    val data = getAllFoldersUnfiltered.execute()
            .groupMap { it.toDisplayableItem() }
            .map {
                val blacklisted = blackListUseCase.get().map { it.toLowerCase() }
                it.map { BlacklistModel(it, blacklisted.contains(it.subtitle!!.toLowerCase())) }
            }

    private fun Folder.toDisplayableItem(): DisplayableItem {
        return DisplayableItem(
                R.layout.dialog_blacklist_item,
                MediaId.folderId(this.path),
                this.title.capitalize(),
                this.path,
                this.image
        )
    }

    fun setDataSet(data: List<BlacklistModel>){
        val blacklisted = data.filter { it.isBlacklisted }
                .mapNotNull { it.displayableItem.subtitle }
                .toSet()
        blackListUseCase.set(blacklisted)
    }


}

class BlacklistModel(
        val displayableItem: DisplayableItem,
        var isBlacklisted: Boolean
)