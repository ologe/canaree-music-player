package dev.olog.presentation.dialog_entry

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.presentation.BR
import dev.olog.presentation._base.BaseAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

class DialogItemAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        mediaId: String,
        private val listPosition: Int

) : BaseAdapter<List<DisplayableItem>>(lifecycle) {

    private val source = MediaIdHelper.mapCategoryToSource(mediaId)

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {

    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.source, source)
        if (position == 0){
            binding.setVariable(BR.position, listPosition)
        } else {
            binding.setVariable(BR.position, position)
        }

    }
}