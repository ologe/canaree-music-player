package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

@PerFragment
class DetailHorizontalAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        mediaId : String

): BaseAdapter(lifecycle) {

    private val source = MediaIdHelper.mapCategoryToSource(mediaId)

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {

    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.position, position)
        binding.setVariable(BR.source, source)
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_detail_album

    fun getItem(position: Int): DisplayableItem = getDataSet()[position]

    fun getItemPosition(mediaId: String) = getDataSet().indexOfFirst { it.mediaId == mediaId }

}