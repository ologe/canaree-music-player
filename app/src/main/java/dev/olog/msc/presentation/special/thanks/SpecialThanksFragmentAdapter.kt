package dev.olog.msc.presentation.special.thanks

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import dev.olog.msc.BR
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import javax.inject.Inject

class SpecialThanksFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle

) : AbsAdapter<SpecialThanksModel>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
    }

    override fun bind(binding: ViewDataBinding, item: SpecialThanksModel, position: Int) {
        binding.setVariable(BR.item,  item)
    }

}