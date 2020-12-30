package dev.olog.feature.edit.playlist.choose

import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olog.core.AppShortcuts
import dev.olog.feature.base.adapter.LayoutContainerViewHolder
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.setOnClickListener
import dev.olog.feature.edit.playlist.R
import dev.olog.lib.image.provider.ImageLoader
import kotlinx.android.synthetic.main.item_playlist_chooser.*

internal class PlaylistChooserActivityAdapter(
    private val activity: FragmentActivity,
    private val appShortcuts: AppShortcuts,
) : ObservableAdapter<PlaylistChooserActivityModel>(PlaylistChooserActivityModelDiff) {

    override fun getItemViewType(position: Int): Int = R.layout.item_playlist_chooser

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            askConfirmation(item)
        }
    }

    private fun askConfirmation(item: PlaylistChooserActivityModel) {

        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.playlist_chooser_dialog_title)
            .setMessage(activity.getString(R.string.playlist_chooser_dialog_message, item.title))
            .setPositiveButton(R.string.popup_positive_ok) { _, _ ->
                appShortcuts.addDetailShortcut(item.mediaId, item.title)
                activity.finish()
            }
            .setNegativeButton(R.string.popup_negative_no, null)
            .show()
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: PlaylistChooserActivityModel,
        position: Int
    ) = holder.bindView {

        ImageLoader.loadAlbumImage(cover, item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
    }
}

private object PlaylistChooserActivityModelDiff : DiffUtil.ItemCallback<PlaylistChooserActivityModel>() {

    override fun areItemsTheSame(
        oldItem: PlaylistChooserActivityModel,
        newItem: PlaylistChooserActivityModel
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: PlaylistChooserActivityModel,
        newItem: PlaylistChooserActivityModel
    ): Boolean {
        return oldItem == newItem
    }
}