package dev.olog.presentation.dialog_entry

import android.app.AlertDialog
import android.content.Context
import android.view.MenuItem
import android.widget.PopupMenu
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.GetDetailTabsVisibilityUseCase
import dev.olog.domain.interactor.detail.SetDetailTabsVisiblityUseCase
import dev.olog.presentation.R
import dev.olog.presentation.dagger.ActivityContext
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.utils.extension.makeDialog
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

open class BaseMenuListener @Inject constructor(
        @ActivityContext private val context: Context,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        private val getDetailTabVisibilityUseCase: GetDetailTabsVisibilityUseCase,
        private val setDetailTabVisibilityUseCase: SetDetailTabsVisiblityUseCase

) : PopupMenu.OnMenuItemClickListener {

    protected lateinit var item: DisplayableItem

    fun setMediaId(item: DisplayableItem): PopupMenu.OnMenuItemClickListener{
        this.item = item
        return this
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        when (itemId) {
            R.id.addToPlaylist -> {
                getSongListByParamUseCase.execute(item.mediaId)
                        .firstOrError()
                        .doOnSuccess { navigator.toAddToPlaylistDialog(item.mediaId, it.size, item.title) }
                        .toCompletable()
                        .subscribe()
            }
            R.id.addToFavorite -> {
                getSongListByParamUseCase.execute(item.mediaId)
                        .firstOrError()
                        .doOnSuccess { navigator.toAddToFavoriteDialog(item.mediaId, it.size, item.title) }
                        .toCompletable()
                        .subscribe()
            }
            R.id.addToQueue -> {
                getSongListByParamUseCase.execute(item.mediaId)
                        .firstOrError()
                        .doOnSuccess { navigator.toAddToQueueDialog(item.mediaId, it.size, item.title) }
                        .toCompletable()
                        .subscribe()
            }
            R.id.delete -> {
                val category = MediaIdHelper.extractCategory(item.mediaId)
                if (category == MediaIdHelper.MEDIA_ID_BY_PLAYLIST){
                    // playlist size not needed
                    navigator.toDeleteDialog(item.mediaId, -1, item.title)
                } else {
                    getSongListByParamUseCase.execute(item.mediaId)
                            .firstOrError()
                            .doOnSuccess { navigator.toDeleteDialog(item.mediaId, it.size, item.title) }
                            .toCompletable()
                            .subscribe()
                }
            }
            Popup.changeDetailTabsVisibility -> {
                createChangeDetailVisibilityDialog()
            }
            else -> return false
        }

        return true
    }

    private fun createChangeDetailVisibilityDialog(){
        val array = arrayOf(
                context.getString(R.string.detail_most_played),
                context.getString(R.string.detail_recently_added),
                context.getString(R.string.related_artists)
        )
        val checkedArray = getDetailTabVisibilityUseCase.execute()
        val checkedList = checkedArray.toMutableList()

        AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.popup_visible_items))
                .setMultiChoiceItems(array, checkedArray, { _, which, isChecked ->
                    checkedList[which] = isChecked
                })
                .setPositiveButton(context.getString(R.string.popup_positive_ok), { _, _ ->
                    setDetailTabVisibilityUseCase.execute(checkedList)
                })
                .setNegativeButton(context.getString(R.string.popup_negative_cancel), null)
                .makeDialog()
    }

}