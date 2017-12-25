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
import dev.olog.shared.constants.DataConstants

object Popup {

    val changeDetailTabsVisibility = View.generateViewId()

    fun create(context: Context, anchor: View, item: DisplayableItem,
               listener: PopupMenu.OnMenuItemClickListener,
               showDetailItem: Boolean = false){

        val popup = PopupMenu(context, anchor, Gravity.BOTTOM or Gravity.END)
        popup.inflate(provideMenuRes(item.mediaId))
        popup.setOnMenuItemClickListener(listener)
        adjustMenu(context, item, popup.menu)
        if (showDetailItem){
            addChangeVisibleTabs(context, popup.menu)
        }
        popup.setOnDismissListener {

        }
        popup.show()
    }

    private fun adjustMenu(context: Context, item: DisplayableItem, menu: Menu){
        val mediaId = item.mediaId
        val category = MediaIdHelper.extractCategory(mediaId)

        val isSong = MediaIdHelper.isSong(mediaId)
        if (isSong){
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
            if (category == MediaIdHelper.MEDIA_ID_BY_ALBUM){
                menu.removeItem(R.id.viewAlbum)
            } else if (category == MediaIdHelper.MEDIA_ID_BY_ARTIST){
                menu.removeItem(R.id.viewArtist)
            }
        } else {
            when (category){
                MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> {
                    val playlistId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
                    when (playlistId){
                        DataConstants.FAVORITE_LIST_ID,
                        DataConstants.HISTORY_LIST_ID,
                        DataConstants.LAST_ADDED_ID -> {
                            menu.removeItem(R.id.rename)
                            menu.removeItem(R.id.delete)
                        }
                    }
                    when (playlistId){
                        DataConstants.LAST_ADDED_ID -> menu.removeItem(R.id.clear)
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

    }

    private fun addChangeVisibleTabs(context: Context, menu: Menu){
        menu.add(Menu.NONE, changeDetailTabsVisibility, Menu.NONE, context.getString(R.string.popup_visible_items))
    }

    @MenuRes
    private fun provideMenuRes(mediaId: String): Int{
        if (MediaIdHelper.isSong(mediaId)){
            return R.menu.dialog_song
        }

        val category = MediaIdHelper.extractCategory(mediaId)
        return when (category){
            MediaIdHelper.MEDIA_ID_BY_FOLDER -> R.menu.dialog_folder
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> R.menu.dialog_playlist
            MediaIdHelper.MEDIA_ID_BY_ALL -> R.menu.dialog_song
            MediaIdHelper.MEDIA_ID_BY_ALBUM -> R.menu.dialog_album
            MediaIdHelper.MEDIA_ID_BY_ARTIST -> R.menu.dialog_artist
            MediaIdHelper.MEDIA_ID_BY_GENRE -> R.menu.dialog_genre
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

}