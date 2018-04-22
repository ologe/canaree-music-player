package dev.olog.msc.presentation.shortcuts.playlist.chooser

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.app.shortcuts.AppShortcuts
import dev.olog.msc.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.k.extension.makeDialog
import dev.olog.msc.utils.k.extension.setOnClickListener
import javax.inject.Inject

class PlaylistChooserActivityAdapter @Inject constructor(
        private val activity: Activity,
        @ActivityLifecycle lifecycle: Lifecycle,
        private var appShortcuts: AppShortcuts

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            askConfirmation(item)
        }
    }

    private fun askConfirmation(item: DisplayableItem) {
        AlertDialog.Builder(activity)
                .setTitle(R.string.playlist_chooser_dialog_title)
                .setMessage(activity.getString(R.string.playlist_chooser_dialog_message, item.title))
                .setPositiveButton(R.string.popup_positive_ok, { _, _ ->
                    appShortcuts.addDetailShortcut(item.mediaId, item.title, item.image)
                    activity.finish()
                })
                .setNegativeButton(R.string.popup_negative_no, null)
                .makeDialog()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }
}