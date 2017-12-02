package dev.olog.presentation.dialog_entry

import android.content.Context
import android.support.annotation.MenuRes
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper

object Popup {

    fun create(context: Context, anchor: View, item: DisplayableItem,
               listener: PopupMenu.OnMenuItemClickListener){

        val popup = PopupMenu(context, anchor, Gravity.BOTTOM or Gravity.END)
        popup.inflate(provideMenuRes(item.mediaId))
        popup.setOnMenuItemClickListener(listener)
        adjustMenu(context, item, popup.menu)

        popup.show()
    }

    private fun adjustMenu(context: Context, item: DisplayableItem, menu: Menu){
        val mediaId = item.mediaId
        val category = MediaIdHelper.extractCategory(mediaId)
        when (category){
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> {
                val playlistId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
                when (playlistId){
                    -5000L, -4000L, -3000L -> menu.removeItem(R.id.rename)
                }
            }
            MediaIdHelper.MEDIA_ID_BY_ALL -> {
                item.subtitle?.let {
                    val unknownAlbum = context.getString(R.string.unknown_album)
                    val unknownArtist = context.getString(R.string.unknown_artist)
                    if (it.contains(unknownAlbum)){
                        menu.removeItem(R.id.viewAlbum)
                    }
                    if (it.contains(unknownArtist)){
                        menu.removeItem(R.id.viewArtist)
                    }
                }
            }
            MediaIdHelper.MEDIA_ID_BY_ALBUM -> {
                item.subtitle?.let {
                    val unknownArtist = context.getString(R.string.unknown_artist)
                    if (it.contains(unknownArtist)){
                        menu.removeItem(R.id.viewArtist)
                    }
                }
            }
        }
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