package dev.olog.presentation.dialog_entry

import android.view.MenuItem
import android.widget.PopupMenu
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import javax.inject.Inject

open class BaseMenuListener @Inject constructor(
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator

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
                getSongListByParamUseCase.execute(item.mediaId)
                        .firstOrError()
                        .doOnSuccess { navigator.toDeleteDialog(item.mediaId, it.size, item.title) }
                        .toCompletable()
                        .subscribe()
            }
        }

        return true
    }
}