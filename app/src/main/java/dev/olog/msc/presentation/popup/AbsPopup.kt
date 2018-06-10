package dev.olog.msc.presentation.popup

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import dev.olog.msc.R
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.presentation.theme.ThemedDialog

abstract class AbsPopup(
        view: View

) : PopupMenu(view.context, view, Gravity.END or Gravity.BOTTOM) {

    companion object {
        val NEW_PLAYLIST_ID = View.generateViewId()
    }

    init {
//        if (view.id == R.id.more){
//            addRotateAnimation(view)
//        }
    }

    fun addPlaylistChooser(context: Context, playlists: List<Playlist>){
        val addToPlaylistMenuItem = menu.findItem(R.id.addToPlaylist)
        val addToPlaylistSubMenu = addToPlaylistMenuItem.subMenu

        playlists.forEach { addToPlaylistSubMenu.add(Menu.NONE, it.id.toInt(), Menu.NONE, it.title) }
        val spannableString = SpannableString("${context.getString(R.string.popup_new_playlist)}..")
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString.length, 0)
        addToPlaylistSubMenu.add(Menu.NONE, NEW_PLAYLIST_ID, Menu.NONE, spannableString)

    }

}