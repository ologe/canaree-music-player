package dev.olog.presentation.dialog_entry

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.Flowable

class DialogItemViewModel(
        context: Context,
        mediaId: String,
        item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
        useCases: Map<String, @JvmSuppressWildcards Completable>

): ViewModel(){

    companion object {
        const val ADD_FAVORITE = "add favorite"
        const val ADD_PLAYLIST = "add playlist"
        const val ADD_QUEUE = "add queue"
        const val VIEW_INFO = "view info"
        const val VIEW_ALBUM = "view album"
        const val VIEW_ARTIST = "view artist"
        const val SHARE = "share"
        const val SET_RINGTONE = "set ringtone"
        const val RENAME = "rename"
        const val DELETE = "delete"
    }

    private val addFavorite = DialogModel(DisplayableItem(R.layout.item_dialog_text, "add favorite", context.getString(R.string.popup_add_to_favorites)),
            useCases[ADD_FAVORITE])
    private val addToPlaylist = DialogModel(DisplayableItem(R.layout.item_dialog_text, "add playlist", context.getString(R.string.popup_add_to_playlist)),
            useCases[ADD_PLAYLIST])
    private val addToQueue = DialogModel(DisplayableItem(R.layout.item_dialog_text, "add queue", context.getString(R.string.popup_add_to_queue)),
            useCases[ADD_QUEUE])
    private val info = DialogModel(DisplayableItem(R.layout.item_dialog_text, "info", context.getString(R.string.popup_view_info)),
            useCases[VIEW_INFO])
    private val viewAlbum = DialogModel(DisplayableItem(R.layout.item_dialog_text, "view album", context.getString(R.string.popup_view_album)),
            useCases[VIEW_ALBUM])
    private val viewArtist = DialogModel(DisplayableItem(R.layout.item_dialog_text, "view artist", context.getString(R.string.popup_view_artist)),
            useCases[VIEW_ARTIST])
    private val share = DialogModel(DisplayableItem(R.layout.item_dialog_text, "share", context.getString(R.string.popup_share)),
            useCases[SHARE])
    private val setRingtone = DialogModel(DisplayableItem(R.layout.item_dialog_text, "set ringtone", context.getString(R.string.popup_set_as_ringtone)),
            useCases[SET_RINGTONE])
    private val rename = DialogModel(DisplayableItem(R.layout.item_dialog_text, "rename", context.getString(R.string.popup_rename)),
            useCases[RENAME])
    private val delete = DialogModel(DisplayableItem(R.layout.item_dialog_text, "delete", context.getString(R.string.popup_delete)),
            useCases[DELETE])

    private val category = MediaIdHelper.extractCategory(mediaId)

    val data : LiveData<List<DialogModel>> = item[category]!!
            .map {
                val result = actions.toMutableList()
                result.add(0, DialogModel(it, null))
                result.toList()
            }.asLiveData()

    private val folderActions : List<DialogModel> = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, delete
    )

    private val playlistActions : List<DialogModel> = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, rename, delete
    )

    private val songActions : List<DialogModel> = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, info,
            viewAlbum, viewArtist, share, setRingtone, delete
    )

    private val albumActions : List<DialogModel> = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, viewArtist, delete
    )

    private val artistActions : List<DialogModel> = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, delete
    )

    private val genreActions : List<DialogModel> = mutableListOf(
            addToPlaylist, addToQueue, addFavorite, delete
    )

    private val actions : List<DialogModel> = when (category){
        MediaIdHelper.MEDIA_ID_BY_FOLDER -> folderActions
        MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> playlistActions
        MediaIdHelper.MEDIA_ID_BY_ALL -> songActions
        MediaIdHelper.MEDIA_ID_BY_ALBUM -> albumActions
        MediaIdHelper.MEDIA_ID_BY_ARTIST -> artistActions
        MediaIdHelper.MEDIA_ID_BY_GENRE -> genreActions
        else -> mutableListOf()
    }

}