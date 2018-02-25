//package dev.olog.msc.presentation.popup
//
//import android.content.Context
//import android.support.annotation.MenuRes
//import android.support.v4.content.ContextCompat
//import android.view.Gravity
//import android.view.View
//import android.widget.ImageButton
//import android.widget.PopupMenu
//import dev.olog.msc.R
//import dev.olog.msc.presentation.model.DisplayableItem
//import dev.olog.msc.utils.MediaId
//import dev.olog.msc.utils.MediaIdCategory
//import javax.inject.Inject
//
//class Popup @Inject constructor(
//){
//
//    companion object {
//
//    }
//
//    fun create(context: Context, anchor: View, item: DisplayableItem,
//               listener: PopupMenu.OnMenuItemClickListener){
//
//        val moreButton = anchor.findViewById<ImageButton>(R.id.more)
//
//
//        val popup = PopupMenu(context, anchor, Gravity.END)
//        popup.inflate(provideMenuRes(item.mediaId))
//        popup.setOnMenuItemClickListener(listener)
//
//        popup.show()
//    }
//
//    @MenuRes
//    private fun provideMenuRes(mediaId: MediaId): Int{
//        if (mediaId.isLeaf){
//            return R.menu.dialog_song
//        }
//
//        return when (mediaId.category){
//            MediaIdCategory.FOLDERS -> R.menu.dialog_folder
//            MediaIdCategory.PLAYLISTS -> R.menu.dialog_playlist
//            MediaIdCategory.ALBUMS -> R.menu.dialog_album
//            MediaIdCategory.ARTISTS -> R.menu.dialog_artist
//            MediaIdCategory.GENRES -> R.menu.dialog_genre
//            else -> throw IllegalArgumentException("invalid media id $mediaId")
//        }
//    }
//}