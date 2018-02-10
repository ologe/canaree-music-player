package dev.olog.msc.presentation.dialog

import android.app.Application
import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.text.TextUtils
import android.view.MenuItem
import android.widget.PopupMenu
import dev.olog.msc.R
import dev.olog.msc.dagger.ProcessLifecycle
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.msc.presentation.MusicController
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.jetbrains.anko.toast
import javax.inject.Inject

open class BaseMenuListener @Inject constructor(
        @ProcessLifecycle lifecycle: Lifecycle,
        private val application: Application,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        private val musicController: MusicController,
        private val getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        private val addToPlaylistUseCase: AddToPlaylistUseCase

) : PopupMenu.OnMenuItemClickListener, DefaultLifecycleObserver {

    protected val subscriptions = CompositeDisposable()

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        subscriptions.clear()
    }

    protected lateinit var item: DisplayableItem

    fun setMediaId(item: DisplayableItem): PopupMenu.OnMenuItemClickListener{
        this.item = item
        return this
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId

        addPlaylistsAsSubItem(itemId)

        when (itemId) {
            Popup.NEW_PLAYLIST_ID -> toCreatePlaylist()
            R.id.play -> playFromMediaId()
            R.id.playShuffle -> playShuffle()
            R.id.addToFavorite -> addToFavorite()
            R.id.addToQueue -> addToQueue()
            R.id.delete -> delete()
            else -> return false
        }

        return true
    }

    private fun addPlaylistsAsSubItem(itemId: Int){
        val playlist = getPlaylistBlockingUseCase.execute()

        playlist.filter { it.id.toInt() == itemId }
                .take(1)
                .forEach { onPlaylistClick(it, item.mediaId).subscribe() }
    }

    private fun toCreatePlaylist(){
        navigator.toCreatePlaylistDialog(item.mediaId)
    }

    private fun playFromMediaId(){
        musicController.playFromMediaId(item.mediaId)
    }

    private fun playShuffle(){
        musicController.playShuffle(item.mediaId)
    }

    private fun addToFavorite(){
        getSongListByParamUseCase.execute(item.mediaId)
                .firstOrError()
                .doOnSuccess { navigator.toAddToFavoriteDialog(item.mediaId, it.size, item.title) }
                .toCompletable()
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    private fun addToQueue(){
        getSongListByParamUseCase.execute(item.mediaId)
                .firstOrError()
                .doOnSuccess { navigator.toAddToQueueDialog(item.mediaId, it.size, item.title) }
                .toCompletable()
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    private fun delete(){
        if (item.mediaId.isPlaylist){
            // playlist does not use size
            navigator.toDeleteDialog(item.mediaId, -1, item.title)
        } else {
            getSongListByParamUseCase.execute(item.mediaId)
                    .firstOrError()
                    .doOnSuccess { navigator.toDeleteDialog(item.mediaId, it.size, item.title) }
                    .toCompletable()
                    .subscribe({}, Throwable::printStackTrace)
                    .addTo(subscriptions)
        }
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