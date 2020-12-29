package dev.olog.presentation.popup

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.Menu
import android.view.View
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Track
import dev.olog.presentation.R
import dev.olog.shared.allTrue
import dev.olog.shared.android.extensions.findActivity
import dev.olog.shared.android.extensions.findChild
import kotlinx.coroutines.*
import me.saket.cascade.CascadePopupMenu
import org.jaudiotagger.audio.AudioFileIO
import java.io.File

abstract class AbsPopup(
    view: View
) : CascadePopupMenu(view.context, view) {

    fun addPlaylistChooser(context: Context, playlists: List<Playlist>){
        val addToPlaylistMenuItem = menu.findItem(R.id.addToPlaylist)
        val addToPlaylistSubMenu = addToPlaylistMenuItem.subMenu

        playlists.forEach { addToPlaylistSubMenu.add(Menu.NONE, it.id.toInt(), Menu.NONE, it.title) }

        val spannableString = SpannableString("${context.getString(R.string.popup_new_playlist)}..")
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString.length, 0)
        addToPlaylistSubMenu.add(
            Menu.NONE,
            R.id.newPlaylist,
            Menu.NONE,
            spannableString
        )

    }

    fun setupViewInfo(
        view: View,
        tracks: suspend () -> List<Track>
    ) {
        val viewInfo = menu.findItem(R.id.viewInfo)
        viewInfo.isEnabled = false

        // viewscope is not available
        view.findActivity().lifecycleScope.launch(Dispatchers.IO) {
            val isEnabled = tracks().map { async { isEditable(it) } }.awaitAll().allTrue()
             setViewInfoEnabled(isEnabled)
        }
    }

    private suspend fun setViewInfoEnabled(isEnabled: Boolean) = withContext(Dispatchers.Main) {
        menu.findItem(R.id.viewInfo).isEnabled = isEnabled
        // workaround, CascadePopupMenu has an immutable Menu
        // so update the internal recyclerview manually
        val menuIndex = menu.children.indexOfFirst { it.itemId == R.id.viewInfo }
        val list = popup.contentView.findChild { it is RecyclerView } as RecyclerView
        list.adapter?.notifyItemChanged(menuIndex)
    }

    private suspend fun isEditable(
        track: Track
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val audioFile = AudioFileIO.read(File(track.path))
            audioFile.tag
            true
        } catch (ex: Throwable) {
            false
        }
    }

}