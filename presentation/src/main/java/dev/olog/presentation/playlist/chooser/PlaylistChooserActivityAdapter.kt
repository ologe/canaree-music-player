package dev.olog.presentation.playlist.chooser

import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.appshortcuts.AppShortcuts
import dev.olog.presentation.R
import dev.olog.shared.compose.component.ComposeListAdapter
import dev.olog.shared.compose.component.ComposeViewHolder
import dev.olog.shared.compose.listitem.ListItemAlbum

class PlaylistChooserActivityAdapter(
    private val activity: FragmentActivity
) : ComposeListAdapter<PlaylistChooserItem>(PlaylistChooserItem) {

    @Composable
    override fun Content(viewHolder: ComposeViewHolder, item: PlaylistChooserItem) {
        ListItemAlbum(
            mediaId = item.mediaId,
            title = item.title,
            subtitle = item.subtitle,
            onClick = { askConfirmation(item) },
        )
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