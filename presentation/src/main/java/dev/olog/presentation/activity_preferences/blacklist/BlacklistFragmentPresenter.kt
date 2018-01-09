package dev.olog.presentation.activity_preferences.blacklist

import dev.olog.domain.entity.Folder
import dev.olog.domain.interactor.GetAllFoldersUnfiltered
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaId
import dev.olog.shared.groupMap
import javax.inject.Inject

class BlacklistFragmentPresenter @Inject constructor(
        getAllFoldersUnfiltered: GetAllFoldersUnfiltered
) {

    val data = getAllFoldersUnfiltered.execute()
            .groupMap { it.toDisplayableItem() }

    private fun Folder.toDisplayableItem(): DisplayableItem {
        return DisplayableItem(
                R.layout.dialog_blacklist_item,
                MediaId.folderId(this.path),
                this.title.capitalize(),
                this.path.capitalize(),
                this.image
        )
    }

}