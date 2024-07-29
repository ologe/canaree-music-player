package dev.olog.presentation.createplaylist


import androidx.compose.runtime.Stable
import dev.olog.core.MediaId
import dev.olog.presentation.base.adapter.ComposeListAdapter
import dev.olog.presentation.base.adapter.ComposeViewHolder

class CreatePlaylistFragmentAdapter(
    private val viewModel: CreatePlaylistFragmentViewModel
) : ComposeListAdapter<CreatePlaylistItem>() {

    override fun bind(holder: ComposeViewHolder, item: CreatePlaylistItem, position: Int) {
        // TODO migrate to compose
    }

//    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
//        viewHolder.setOnClickListener(this) { item, _, view ->
//            val checkBox = view.findViewById<CheckBox>(R.id.selected)
//            val wasChecked = checkBox.isChecked
//            checkBox.isChecked = !wasChecked
//            viewModel.toggleItem(item.mediaId)
//        }
//    }

//    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
//        require(item is DisplayableTrack)
//
//        holder.itemView.apply {
//            selected.isChecked = viewModel.isChecked(item.mediaId)
//            BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
//            firstText.text = item.title
//            secondText.text = item.subtitle
//        }
//    }
}

@Stable
data class CreatePlaylistItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
    val isChecked: Boolean,
)