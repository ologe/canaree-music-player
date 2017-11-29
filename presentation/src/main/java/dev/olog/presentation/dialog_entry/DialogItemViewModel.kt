package dev.olog.presentation.dialog_entry

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable

class DialogItemViewModel(
        context: Context,
        mediaId: String,
        item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>

): ViewModel(){

    private val play = DisplayableItem(R.layout.item_dialog_text, "play", context.getString(R.string.popup_play), "${R.drawable.vd_play}")
    private val shuffle = DisplayableItem(R.layout.item_dialog_text, "shuffle", context.getString(R.string.popup_play_shuffle), "${R.drawable.vd_shuffle}")
    private val addFavorite = DisplayableItem(R.layout.item_dialog_text, "add favorite", context.getString(R.string.popup_add_to_favorites), "${R.drawable.vd_favorite}")
    private val addToPlaylist = DisplayableItem(R.layout.item_dialog_text, "add playlist", context.getString(R.string.popup_add_to_playlist), "${R.drawable.vd_playlist_add}")
    private val addToQueue = DisplayableItem(R.layout.item_dialog_text, "add queue", context.getString(R.string.popup_add_to_queue), "${R.drawable.vd_queue_music}")
    private val info = DisplayableItem(R.layout.item_dialog_text, "info", context.getString(R.string.popup_info), "${R.drawable.vd_info}")
    private val viewAlbum = DisplayableItem(R.layout.item_dialog_text, "view album", context.getString(R.string.popup_view_album), "${R.drawable.vd_album_dark}")
    private val viewArtist = DisplayableItem(R.layout.item_dialog_text, "view artist", context.getString(R.string.popup_view_artist), "${R.drawable.vd_artist_dark}")
    private val share = DisplayableItem(R.layout.item_dialog_text, "share", context.getString(R.string.popup_share), "${R.drawable.vd_share}")
    private val setRingtone = DisplayableItem(R.layout.item_dialog_text, "set ringtone", context.getString(R.string.popup_set_as_ringtone), "${R.drawable.vd_bell}")
    private val rename = DisplayableItem(R.layout.item_dialog_text, "rename", context.getString(R.string.popup_rename), "${R.drawable.vd_font}")
    private val delete = DisplayableItem(R.layout.item_dialog_text, "delete", context.getString(R.string.popup_delete), "${R.drawable.vd_delete}")

    private val category = MediaIdHelper.extractCategory(mediaId)

    val data : LiveData<List<DisplayableItem>> = item[category]!!
            .map {
                val result = actions.toMutableList()
                result.add(0, it)
                result.toList()
            }.asLiveData()

    private val folderActions = mutableListOf(
            play, shuffle, addToPlaylist, addToQueue, delete
    )

    private val playlistActions = mutableListOf(
            play, shuffle, addToPlaylist, addToQueue, rename, delete
    )

    private val songActions = mutableListOf(
            addToPlaylist, addFavorite, addToQueue, info,
            viewAlbum, viewArtist, share, setRingtone, delete
    )

    private val albumActions = mutableListOf(
            play, shuffle, addToPlaylist, addToQueue, viewArtist, share, delete
    )

    private val artistActions = mutableListOf(
            play, shuffle, addToPlaylist, addToQueue, share, delete
    )

    private val genreActions = mutableListOf(
            play, shuffle, addToPlaylist, addToQueue, delete
    )

    private val actions = when (category){
        MediaIdHelper.MEDIA_ID_BY_FOLDER -> folderActions
        MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> playlistActions
        MediaIdHelper.MEDIA_ID_BY_ALL -> songActions
        MediaIdHelper.MEDIA_ID_BY_ALBUM -> albumActions
        MediaIdHelper.MEDIA_ID_BY_ARTIST -> artistActions
        MediaIdHelper.MEDIA_ID_BY_GENRE -> genreActions
        else -> mutableListOf()
    }

}