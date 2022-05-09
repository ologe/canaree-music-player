package dev.olog.presentation.createplaylist


import android.widget.CheckBox
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.ui.model.DiffCallbackDisplayableItem
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.ui.model.DisplayableItem
import dev.olog.ui.model.DisplayableTrack
import kotlinx.android.synthetic.main.item_create_playlist.view.*

class CreatePlaylistFragmentAdapter(
    private val viewModel: CreatePlaylistFragmentViewModel
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, view ->
            val checkBox = view.findViewById<CheckBox>(R.id.selected)
            val wasChecked = checkBox.isChecked
            checkBox.isChecked = !wasChecked
            viewModel.toggleItem(item.mediaId)
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        require(item is DisplayableTrack)

        holder.itemView.apply {
            selected.isChecked = viewModel.isChecked(item.mediaId)
            BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
        }
    }
}