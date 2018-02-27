package dev.olog.msc.presentation.licenses

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.text.method.LinkMovementMethod
import dev.olog.msc.BR
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.databinding.ItemLicenseBinding
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import javax.inject.Inject

class LicensesFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle

) : AbsAdapter<LicenseModel>(lifecycle){

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        (viewHolder.binding as ItemLicenseBinding)
                .url.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun bind(binding: ViewDataBinding, item: LicenseModel, position: Int) {
        binding.setVariable(BR.license, item)
    }

}