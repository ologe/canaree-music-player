package dev.olog.presentation.dialog_entry

import io.reactivex.Completable
import javax.inject.Inject

class DialogItemViewModel @Inject constructor(
        val data: Map<String, Completable>

) {

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

}