package dev.olog.presentation.dialog_entry

import android.app.Application
import android.text.TextUtils
import android.view.MenuItem
import android.widget.PopupMenu
import dev.olog.domain.entity.Playlist
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_music.MusicController
import dev.olog.shared.MediaId
import io.reactivex.Completable
import org.jetbrains.anko.toast
import javax.inject.Inject

open class BaseMenuListener @Inject constructor(
        private val application: Application,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        private val musicController: MusicController,
        private val getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        private val addToPlaylistUseCase: AddToPlaylistUseCase

) : PopupMenu.OnMenuItemClickListener {

    protected lateinit var item: DisplayableItem

    fun setMediaId(item: DisplayableItem): PopupMenu.OnMenuItemClickListener{
        this.item = item
        return this
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        val playlist = getPlaylistBlockingUseCase.execute()

        playlist.filter { it.id.toInt() == itemId }
                .take(1)
                .forEach { onPlaylistClick(it, item.mediaId).subscribe() }

        when (itemId) {
            Popup.NEW_PLAYLIST_ID -> navigator.toCreatePlaylistDialog(item.mediaId)
            R.id.play -> musicController.playFromMediaId(item.mediaId)
            R.id.playShuffle -> musicController.playShuffle(item.mediaId)

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
                if (item.mediaId.isPlaylist){
                    // playlist does not use size
                    navigator.toDeleteDialog(item.mediaId, -1, item.title)
                } else {
                    getSongListByParamUseCase.execute(item.mediaId)
                            .firstOrError()
                            .doOnSuccess { navigator.toDeleteDialog(item.mediaId, it.size, item.title) }
                            .toCompletable()
                            .subscribe()
                }
            }
            else -> return false
        }

        return true
    }

    private fun onPlaylistClick(playlist: Playlist, mediaId: MediaId): Completable {

        return addToPlaylistUseCase.execute(Pair(playlist, mediaId))
                .doOnSuccess { createSuccessMessage(it) }
                .doOnError { createErrorMessage() }
                .toCompletable()
    }

    private fun createSuccessMessage(pairStringPlaylistName: Pair<String, String>){
        val (string, playlistTitle) = pairStringPlaylistName
        val message = if (TextUtils.isDigitsOnly(string)){
            val size = string.toInt()
            application.resources.getQuantityString(R.plurals.xx_songs_added_to_playlist_y, size, size, playlistTitle)
        } else {
            application.getString(R.string.added_song_x_to_playlist_y, string, playlistTitle)
        }
        application.toast(message)
    }

    private fun createErrorMessage(){
        application.toast(application.getString(R.string.popup_error_message))
    }


}