package dev.olog.presentation.fragment_special_thanks

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.presentation.BR
import dev.olog.presentation._base.list.BaseListAdapter
import dev.olog.presentation._base.list.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import javax.inject.Inject

class SpecialThanksFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle

) : BaseListAdapter<SpecialThanksModel>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
    }

    override fun bind(binding: ViewDataBinding, item: SpecialThanksModel, position: Int) {
        binding.setVariable(BR.item,  item)
    }

}