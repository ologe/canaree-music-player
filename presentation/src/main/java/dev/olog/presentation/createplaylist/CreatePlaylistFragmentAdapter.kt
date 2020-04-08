package dev.olog.presentation.createplaylist


import android.widget.CheckBox
import dev.olog.presentation.R
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.adapter.DiffCallbackDisplayableTrack
import dev.olog.feature.presentation.base.adapter.ObservableAdapter
import dev.olog.feature.presentation.base.adapter.setOnClickListener
import dev.olog.presentation.loadSongImage
import dev.olog.feature.presentation.base.model.DisplayableTrack
import dev.olog.feature.presentation.base.model.toDomain
import kotlinx.android.synthetic.main.item_create_playlist.view.*

class CreatePlaylistFragmentAdapter(
    private val viewModel: CreatePlaylistFragmentViewModel
) : ObservableAdapter<DisplayableTrack>(DiffCallbackDisplayableTrack) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, view ->
            val checkBox = view.findViewById<CheckBox>(R.id.selected)
            val wasChecked = checkBox.isChecked
            checkBox.isChecked = !wasChecked
            viewModel.toggleItem(item.mediaId)
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableTrack, position: Int) {

        holder.itemView.apply {
            selected.isChecked = viewModel.isChecked(item.mediaId)
            holder.imageView!!.loadSongImage(item.mediaId.toDomain())
            firstText.text = item.title
            secondText.text = item.subtitle
        }
    }
}