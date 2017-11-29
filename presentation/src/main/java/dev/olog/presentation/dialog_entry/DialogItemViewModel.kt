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

    private val addFavorite = DialogModel(DisplayableItem(R.layout.item_dialog_text, "add favorite", context.getString(R.string.popup_add_to_favorites)),
            )
    private val addToPlaylist = DisplayableItem(R.layout.item_dialog_text, "add playlist", context.getString(R.string.popup_add_to_playlist))
    private val addToQueue = DisplayableItem(R.layout.item_dialog_text, "add queue", context.getString(R.string.popup_add_to_queue))
    private val info = DisplayableItem(R.layout.item_dialog_text, "info", context.getString(R.string.popup_info))
    private val viewAlbum = DisplayableItem(R.layout.item_dialog_text, "view album", context.getString(R.string.popup_view_album))
    private val viewArtist = DisplayableItem(R.layout.item_dialog_text, "view artist", context.getString(R.string.popup_view_artist))
    private val share = DisplayableItem(R.layout.item_dialog_text, "share", context.getString(R.string.popup_share))
    private val setRingtone = DisplayableItem(R.layout.item_dialog_text, "set ringtone", context.getString(R.string.popup_set_as_ringtone))
    private val rename = DisplayableItem(R.layout.item_dialog_text, "rename", context.getString(R.string.popup_rename))
    private val delete = DisplayableItem(R.layout.item_dialog_text, "delete", context.getString(R.string.popup_delete))

    private val category = MediaIdHelper.extractCategory(mediaId)

    val data : LiveData<List<DialogModel>> = item[category]!!
            .map {
                val result = actions.toMutableList()
                result.add(0, it)
                result.toList()
            }.asLiveData()

    private val folderActions = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, delete
    )

    private val playlistActions = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, rename, delete
    )

    private val songActions = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, info,
            viewAlbum, viewArtist, share, setRingtone, delete
    )

    private val albumActions = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, viewArtist, share, delete
    )

    private val artistActions = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, share, delete
    )

    private val genreActions = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, delete
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