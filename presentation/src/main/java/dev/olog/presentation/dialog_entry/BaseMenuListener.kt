package dev.olog.presentation.dialog_entry

import android.view.MenuItem
import android.widget.PopupMenu
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.presentation.R
import dev.olog.presentation.navigation.Navigator
import javax.inject.Inject

open class BaseMenuListener @Inject constructor(
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator

) : PopupMenu.OnMenuItemClickListener {

    protected lateinit var mediaId: String

    fun setMediaId(mediaId: String): PopupMenu.OnMenuItemClickListener{
        this.mediaId = mediaId
        return this
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.addToPlaylist -> {
                getSongListByParamUseCase.execute(mediaId)
                        .firstOrError()
                        .doOnSuccess { navigator.toAddToPlaylistDialog(mediaId, it.size, "aa") }
                        .toCompletable()
                        .subscribe()
            }
            R.id.addToFavorite -> {
                getSongListByParamUseCase.execute(mediaId)
                        .firstOrError()
                        .doOnSuccess { navigator.toAddToFavoriteDialog(mediaId, it.size, "aa") }
                        .toCompletable()
                        .subscribe()
            }
            R.id.addToQueue -> {
                getSongListByParamUseCase.execute(mediaId)
                        .firstOrError()
                        .doOnSuccess { navigator.toAddToQueueDialog(mediaId, it.size, "aa") }
                        .toCompletable()
                        .subscribe()
            }
            R.id.delete -> {
                getSongListByParamUseCase.execute(mediaId)
                        .firstOrError()
                        .doOnSuccess { navigator.toDeleteDialog(mediaId, it.size, "aa") }
                        .toCompletable()
                        .subscribe()
            }
        }

        return true
    }
}