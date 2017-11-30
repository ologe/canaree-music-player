package dev.olog.presentation.dialog_entry

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import dev.olog.presentation.BR
import dev.olog.presentation._base.BaseListAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

class DialogItemAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        mediaId: String,
        private val listPosition: Int,
        private val view: DialogItemView

) : BaseListAdapter<DialogModel>(lifecycle) {

    private val source = MediaIdHelper.mapCategoryToSource(mediaId)

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position != RecyclerView.NO_POSITION){
                dataController[position].useCase
                        ?.subscribe(view::dismiss, Throwable::printStackTrace)
            }
        }
    }

    override fun bind(binding: ViewDataBinding, item: DialogModel, position: Int) {
        binding.setVariable(BR.item, item.item)
        binding.setVariable(BR.source, source)

        if (position == 0){
            binding.setVariable(BR.position, listPosition)
        } else {
            binding.setVariable(BR.position, position)
        }
    }

    override fun getItemViewType(position: Int): Int = dataController[position].item.type

    override fun areItemsTheSame(oldItem: DialogModel, newItem: DialogModel): Boolean {
        return oldItem.item.mediaId == newItem.item.mediaId
    }
}