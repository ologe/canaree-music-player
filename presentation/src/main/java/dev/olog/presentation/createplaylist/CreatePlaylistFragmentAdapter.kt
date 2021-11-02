package dev.olog.presentation.createplaylist


import android.widget.CheckBox
import androidx.lifecycle.Lifecycle
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.feature.base.adapter.DataBoundViewHolder
import dev.olog.feature.base.adapter.DiffCallbackDisplayableItem
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.setOnClickListener
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.feature.base.model.DisplayableTrack
import kotlinx.android.synthetic.main.item_create_playlist.view.*

class CreatePlaylistFragmentAdapter(
    lifecycle: Lifecycle,
    private val viewModel: CreatePlaylistFragmentViewModel

) : ObservableAdapter<DisplayableItem>(lifecycle, DiffCallbackDisplayableItem) {

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