package dev.olog.msc.presentation.shortcuts.playlist.chooser

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.appshortcuts.AppShortcuts
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.DiffCallbackDisplayableItem
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.base.adapter.setOnClickListener
import dev.olog.presentation.dagger.ActivityLifecycle
import dev.olog.presentation.model.DisplayableItem
import javax.inject.Inject

class PlaylistChooserActivityAdapter @Inject constructor(
    private val activity: Activity,
    @ActivityLifecycle lifecycle: Lifecycle

) : ObservableAdapter<DisplayableItem>(lifecycle,
    DiffCallbackDisplayableItem
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            askConfirmation(item)
        }
    }

    private fun askConfirmation(item: DisplayableItem) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.playlist_chooser_dialog_title)
            .setMessage(activity.getString(R.string.playlist_chooser_dialog_message, item.title))
            .setPositiveButton(R.string.popup_positive_ok) { _, _ ->
                AppShortcuts.instance(activity).addDetailShortcut(item.mediaId, item.title)
                activity.finish()
            }
            .setNegativeButton(R.string.popup_negative_no, null)
            .show()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }
}