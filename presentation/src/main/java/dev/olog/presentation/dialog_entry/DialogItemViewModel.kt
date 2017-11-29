package dev.olog.presentation.dialog_entry

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable

class DialogItemViewModel(
        context: Context,
        mediaId: String,
        item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>

): ViewModel(){

    private val category = MediaIdHelper.extractCategory(mediaId)

    val data : LiveData<List<DisplayableItem>> = item[category]!!
            .map {
                val result = actions.toMutableList()
                result.add(0, it)
                result.toList()
            }.asLiveData()

    private val folderActions = mutableListOf(
            DisplayableItem(R.layout.item_dialog_text, "play", context.getString(R.string.popup_play)),
            DisplayableItem(R.layout.item_dialog_text, "shuffle", context.getString(R.string.popup_play_shuffle)),
            DisplayableItem(R.layout.item_dialog_text, "add playlist", context.getString(R.string.popup_add_to_playlist)),
            DisplayableItem(R.layout.item_dialog_text, "add queue", context.getString(R.string.popup_add_to_queue)),
            DisplayableItem(R.layout.item_dialog_text, "delete", context.getString(R.string.popup_delete))
    )

    private val playlistActions = mutableListOf(
            DisplayableItem(R.layout.item_dialog_text, "play", context.getString(R.string.popup_play)),
            DisplayableItem(R.layout.item_dialog_text, "shuffle", context.getString(R.string.popup_play_shuffle)),
            DisplayableItem(R.layout.item_dialog_text, "add playlist", context.getString(R.string.popup_add_to_playlist)),
            DisplayableItem(R.layout.item_dialog_text, "add queue", context.getString(R.string.popup_add_to_queue)),
            DisplayableItem(R.layout.item_dialog_text, "rename", context.getString(R.string.popup_rename)),
            DisplayableItem(R.layout.item_dialog_text, "delete", context.getString(R.string.popup_delete))
    )

    private val songActions = mutableListOf(
            DisplayableItem(R.layout.item_dialog_text, "add playlist", context.getString(R.string.popup_add_to_playlist)),
            DisplayableItem(R.layout.item_dialog_text, "add favorite", context.getString(R.string.popup_add_to_favorites)),
            DisplayableItem(R.layout.item_dialog_text, "add queue", context.getString(R.string.popup_add_to_queue)),
            DisplayableItem(R.layout.item_dialog_text, "info", context.getString(R.string.popup_info)),
            DisplayableItem(R.layout.item_dialog_text, "view album", context.getString(R.string.popup_view_album)),
            DisplayableItem(R.layout.item_dialog_text, "view artist", context.getString(R.string.popup_view_artist)),
            DisplayableItem(R.layout.item_dialog_text, "share", context.getString(R.string.popup_share)),
            DisplayableItem(R.layout.item_dialog_text, "set ringtone", context.getString(R.string.popup_set_as_ringtone)),
            DisplayableItem(R.layout.item_dialog_text, "delete", context.getString(R.string.popup_delete))
    )

    private val albumActions = mutableListOf(
            DisplayableItem(R.layout.item_dialog_text, "play", context.getString(R.string.popup_play)),
            DisplayableItem(R.layout.item_dialog_text, "shuffle", context.getString(R.string.popup_play_shuffle)),
            DisplayableItem(R.layout.item_dialog_text, "add playlist", context.getString(R.string.popup_add_to_playlist)),
            DisplayableItem(R.layout.item_dialog_text, "add queue", context.getString(R.string.popup_add_to_queue)),
            DisplayableItem(R.layout.item_dialog_text, "info", context.getString(R.string.popup_info)),
            DisplayableItem(R.layout.item_dialog_text, "view artist", context.getString(R.string.popup_view_artist)),
            DisplayableItem(R.layout.item_dialog_text, "share", context.getString(R.string.popup_share)),
            DisplayableItem(R.layout.item_dialog_text, "delete", context.getString(R.string.popup_delete))
    )

    private val artistActions = mutableListOf(
            DisplayableItem(R.layout.item_dialog_text, "play", context.getString(R.string.popup_play)),
            DisplayableItem(R.layout.item_dialog_text, "shuffle", context.getString(R.string.popup_play_shuffle)),
            DisplayableItem(R.layout.item_dialog_text, "add playlist", context.getString(R.string.popup_add_to_playlist)),
            DisplayableItem(R.layout.item_dialog_text, "add queue", context.getString(R.string.popup_add_to_queue)),
            DisplayableItem(R.layout.item_dialog_text, "share", context.getString(R.string.popup_share)),
            DisplayableItem(R.layout.item_dialog_text, "delete", context.getString(R.string.popup_delete))
    )

    private val genreActions = mutableListOf(
            DisplayableItem(R.layout.item_dialog_text, "play", context.getString(R.string.popup_play)),
            DisplayableItem(R.layout.item_dialog_text, "shuffle", context.getString(R.string.popup_play_shuffle)),
            DisplayableItem(R.layout.item_dialog_text, "add playlist", context.getString(R.string.popup_add_to_playlist)),
            DisplayableItem(R.layout.item_dialog_text, "add queue", context.getString(R.string.popup_add_to_queue)),
            DisplayableItem(R.layout.item_dialog_text, "delete", context.getString(R.string.popup_delete))
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