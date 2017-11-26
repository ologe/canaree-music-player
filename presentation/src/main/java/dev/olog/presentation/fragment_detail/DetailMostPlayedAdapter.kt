package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.presentation.BR
import dev.olog.presentation._base.BaseAdapter
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.setOnClickListener
import javax.inject.Inject

@PerFragment
class DetailMostPlayedAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val viewModel: DetailFragmentViewModel

) : BaseAdapter(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        viewHolder.setOnClickListener(getDataSet(), { item ->
            viewModel.addToMostPlayed(item.mediaId)
                    .subscribe()
        })
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int){
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.position, position)
    }

}