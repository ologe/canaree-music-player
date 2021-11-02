package dev.olog.presentation.playlist.chooser

import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.appshortcuts.AppShortcuts
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.feature.base.adapter.DataBoundViewHolder
import dev.olog.feature.base.adapter.DiffCallbackDisplayableItem
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.setOnClickListener
import dev.olog.feature.base.model.DisplayableAlbum
import dev.olog.feature.base.model.DisplayableItem
import kotlinx.android.synthetic.main.item_tab_album.view.*

class PlaylistChooserActivityAdapter(
    private val activity: FragmentActivity

) : ObservableAdapter<DisplayableItem>(
    activity.lifecycle,
    DiffCallbackDisplayableItem
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            askConfirmation(item)
        }
    }

    private fun askConfirmation(item: DisplayableItem) {
        require(item is DisplayableAlbum)

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

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        require(item is DisplayableAlbum)

        holder.itemView.apply {
            BindingsAdapter.loadAlbumImage(holder.imageView!!, item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
        }
    }
}