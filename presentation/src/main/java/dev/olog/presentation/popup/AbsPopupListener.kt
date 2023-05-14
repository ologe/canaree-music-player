package dev.olog.presentation.popup

import android.content.Context
import androidx.appcompat.widget.PopupMenu
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.QueryMode
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.interactor.playlist.AddToPlaylistUseCase
import dev.olog.platform.extension.toast
import dev.olog.presentation.R
import dev.olog.presentation.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class AbsPopupListener(
    private val playlistGateway: PlaylistGateway,
    private val addToPlaylistUseCase: AddToPlaylistUseCase,
) : PopupMenu.OnMenuItemClickListener {

    fun getPlaylists(isPodcast: Boolean): List<Playlist>  {
        val mode = if (isPodcast) QueryMode.Podcasts else QueryMode.Songs
        return playlistGateway.getAll(mode)
    }

    protected fun onPlaylistSubItemClick(
        context: Context,
        itemId: Int,
        mediaId: MediaId,
        listSize: Int,
        title: String
    ) {
        val playlist = getPlaylists(mediaId.isPodcast).find { it.id == itemId.toLong() } ?: return
        GlobalScope.launch(Dispatchers.Main) {
            try {
                addToPlaylistUseCase(playlist, mediaId)
                createSuccessMessage(
                    context = context,
                    playlist = playlist,
                    mediaId = mediaId,
                    listSize = listSize,
                    title = title
                )
            } catch (ex: Throwable){
                createErrorMessage(context)
            }
        }
    }

    private fun createSuccessMessage(
        context: Context,
        playlist: Playlist,
        mediaId: MediaId,
        listSize: Int,
        title: String
    ) {
        val playlistTitle = playlist.title
        val message = when (mediaId.category) {
            MediaIdCategory.SONGS -> context.getString(R.string.added_song_x_to_playlist_y, title, playlistTitle)
            else -> context.resources.getQuantityString(
                R.plurals.xx_songs_added_to_playlist_y,
                listSize,
                listSize,
                playlistTitle
            )
        }
        context.toast(message)
    }

    private suspend fun createErrorMessage(context: Context) = withContext(Dispatchers.Main) {
        context.toast(context.getString(R.string.popup_error_message))
    }

    protected fun viewInfo(navigator: Navigator, mediaId: MediaId) {
        navigator.toEditInfoFragment(mediaId)
    }

    protected fun viewAlbum(navigator: Navigator, mediaId: MediaId) {
        navigator.toDetailFragment(mediaId)
    }

    protected fun viewArtist(navigator: Navigator, mediaId: MediaId) {
        navigator.toDetailFragment(mediaId)
    }


}