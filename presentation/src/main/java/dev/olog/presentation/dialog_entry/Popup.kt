package dev.olog.presentation.dialog_entry

import android.content.Context
import android.support.annotation.MenuRes
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import dev.olog.presentation.R
import dev.olog.shared.MediaIdHelper

object Popup {

    fun create(context: Context, anchor: View, mediaId: String,
               listener: PopupMenu.OnMenuItemClickListener){

        val popup = PopupMenu(context, anchor, Gravity.BOTTOM)
        popup.inflate(provideMenuRes(mediaId))
        popup.setOnMenuItemClickListener(listener)

        popup.show()
    }

    @MenuRes
    private fun provideMenuRes(mediaId: String): Int{
        val category = MediaIdHelper.extractCategory(mediaId)
        return when (category){
            MediaIdHelper.MEDIA_ID_BY_FOLDER -> R.menu.dialog_folder
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> R.menu.dialog_playlist
            MediaIdHelper.MEDIA_ID_BY_ALL -> R.menu.dialog_song
            MediaIdHelper.MEDIA_ID_BY_ALBUM -> R.menu.dialog_album
            MediaIdHelper.MEDIA_ID_BY_ARTIST -> R.menu.dialog_artist
            MediaIdHelper.MEDIA_ID_BY_GENRE -> R.menu.dialog_genre
            else -> throw IllegalArgumentException("invalid media id$mediaId")
        }
    }

}