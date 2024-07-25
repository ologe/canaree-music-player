package dev.olog.presentation.playlist.chooser

import androidx.compose.runtime.Stable
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.appshortcuts.AppShortcuts
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.ComposeListAdapter
import dev.olog.presentation.base.adapter.ComposeViewHolder
import dev.olog.shared.compose.list.ListItemAlbum

class PlaylistChooserActivityAdapter(
    private val activity: FragmentActivity

) : ComposeListAdapter<PlaylistChooserItem>() {

    override fun bind(holder: ComposeViewHolder, item: PlaylistChooserItem, position: Int) {
        holder.setContent {
            ListItemAlbum(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle,
                showQuickAction = false,
                onClick = { askConfirmation(item) },
                onLongClick = null,
            )
        }
    }

    private fun askConfirmation(item: PlaylistChooserItem) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.playlist_chooser_dialog_title)
            .setMessage(activity.getString(R.string.playlist_chooser_dialog_message, item.title))
            .setPositiveButton(R.string.popup_positive_ok) { _, _ ->
                AppShortcuts.instance(activity).addDetailShortcut(item.mediaId, item.title)
                activity.finish()
            }
            .setNegativeButton(R.string.popup_negative_no, null)
            .show()
    }


}

@Stable
data class PlaylistChooserItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)