package dev.olog.presentation.dialog_entry

import android.content.Context
import android.graphics.Typeface
import android.support.annotation.MenuRes
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import dev.olog.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaId
import dev.olog.shared.MediaIdCategory
import dev.olog.shared_android.Constants
import javax.inject.Inject

class Popup @Inject constructor(
        private val getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase
){

    companion object {
        val NEW_PLAYLIST_ID = View.generateViewId()
    }

    fun create(context: Context, anchor: View, item: DisplayableItem,
               listener: PopupMenu.OnMenuItemClickListener){

        val popup = PopupMenu(context, anchor, Gravity.BOTTOM or Gravity.END)
        popup.inflate(provideMenuRes(item.mediaId))
        popup.setOnMenuItemClickListener(listener)
        adjustMenu(context, item, popup.menu)

        val menu = popup.menu
        val addToPlaylistMenuItem = menu.findItem(R.id.addToPlaylist)
        val addToPlaylistSubMenu = addToPlaylistMenuItem.subMenu

        val playlists = getPlaylistBlockingUseCase.execute()

        playlists.forEach { addToPlaylistSubMenu.add(Menu.NONE, it.id.toInt(), Menu.NONE, it.title) }
        val spannableString = SpannableString("${context.getString(R.string.popup_new_playlist)}..")
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString.length, 0)
        addToPlaylistSubMenu.add(Menu.NONE, NEW_PLAYLIST_ID, Menu.NONE, spannableString)

        popup.show()
    }

    private fun adjustMenu(context: Context, item: DisplayableItem, menu: Menu){
        val mediaId = item.mediaId

        if (item.mediaId.isLeaf){
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
            if (item.mediaId.isAlbum){
                menu.removeItem(R.id.viewAlbum)
            } else if (item.mediaId.isArtist){
                menu.removeItem(R.id.viewArtist)
            }
        } else {
            when (item.mediaId.category){
                MediaIdCategory.PLAYLIST -> {
                    val playlistId = item.mediaId.categoryValue.toLong()
                    when (playlistId){
                        Constants.FAVORITE_LIST_ID,
                        Constants.HISTORY_LIST_ID,
                        Constants.LAST_ADDED_ID -> {
                            menu.removeItem(R.id.rename)
                            menu.removeItem(R.id.delete)
                        }
                    }
                    when (playlistId){
                        Constants.LAST_ADDED_ID -> menu.removeItem(R.id.clear)
                    }
                }
                MediaIdCategory.ALBUM -> {
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

    @MenuRes
    private fun provideMenuRes(mediaId: MediaId): Int{
        if (mediaId.isLeaf){
            return R.menu.dialog_song
        }

        return when (mediaId.category){
            MediaIdCategory.FOLDER -> R.menu.dialog_folder
            MediaIdCategory.PLAYLIST -> R.menu.dialog_playlist
//            MediaIdCategory.ALL -> R.menu.dialog_song
            MediaIdCategory.ALBUM -> R.menu.dialog_album
            MediaIdCategory.ARTIST -> R.menu.dialog_artist
            MediaIdCategory.GENRE -> R.menu.dialog_genre
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

//    private fun addChangeVisibleTabs(context: Context, menu: Menu){
//        menu.add(Menu.NONE, changeDetailTabsVisibility, Menu.NONE, context.getString(R.string.popup_visible_items))
//    }

//    private fun createChangeDetailVisibilityDialog(){
//        val array = arrayOf(
//                context.getString(R.string.detail_most_played),
//                context.getString(R.string.detail_recently_added),
//                context.getString(R.string.related_artists)
//        )
//        val checkedArray = getDetailTabVisibilityUseCase.execute()
//        val checkedList = checkedArray.toMutableList()
//
//        AlertDialog.Builder(context)
//                .setTitle(context.getString(R.string.popup_visible_items))
//                .setMultiChoiceItems(array, checkedArray, { _, which, isChecked ->
//                    checkedList[which] = isChecked
//                })
//                .setPositiveButton(context.getString(R.string.popup_positive_ok), { _, _ ->
//                    setDetailTabVisibilityUseCase.execute(checkedList)
//                })
//                .setNegativeButton(context.getString(R.string.popup_negative_cancel), null)
//                .makeDialog()
//    }
}