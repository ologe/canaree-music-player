package dev.olog.msc.presentation.preferences.blacklist

import dev.olog.msc.R
import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.interactor.prefs.BlackListUseCase
import dev.olog.msc.domain.interactor.prefs.GetAllFoldersUnfiltered
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.groupMap
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