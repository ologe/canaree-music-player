package dev.olog.feature.edit.playlist.create

import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.base.adapter.LayoutContainerViewHolder
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.setOnClickListener
import dev.olog.feature.edit.playlist.R
import dev.olog.lib.image.provider.ImageLoader
import kotlinx.android.synthetic.main.item_create_playlist.*

internal class CreatePlaylistFragmentAdapter(
    private val viewModel: CreatePlaylistFragmentViewModel
) : ObservableAdapter<CreatePlaylistFragmentModel>(CreatePlaylistFragmentModelDiff) {

    override fun getItemViewType(position: Int): Int = R.layout.item_create_playlist

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, view ->
            val checkBox = view.findViewById<CheckBox>(R.id.selected)
            val wasChecked = checkBox.isChecked
            checkBox.isChecked = !wasChecked
            viewModel.toggleItem(item.mediaId)
        }
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: CreatePlaylistFragmentModel,
        position: Int
    ) = holder.bindView {

        selected.isChecked = viewModel.isChecked(item.mediaId)
        ImageLoader.loadSongImage(imageView!!, item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
    }
}

private object CreatePlaylistFragmentModelDiff : DiffUtil.ItemCallback<CreatePlaylistFragmentModel>() {

    override fun areItemsTheSame(
        oldItem: CreatePlaylistFragmentModel,
        newItem: CreatePlaylistFragmentModel
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: CreatePlaylistFragmentModel,
        newItem: CreatePlaylistFragmentModel
    ): Boolean {
        return oldItem == newItem
    }
}