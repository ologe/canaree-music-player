package dev.olog.presentation.fragment_licenses

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.text.method.LinkMovementMethod
import dev.olog.presentation.BR
import dev.olog.presentation._base.list.BaseListAdapter
import dev.olog.presentation._base.list.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.databinding.ItemLicenseBinding
import javax.inject.Inject

class LicensesFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle

) : BaseListAdapter<LicenseModel>(lifecycle){

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        (viewHolder.binding as ItemLicenseBinding)
                .url.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun bind(binding: ViewDataBinding, item: LicenseModel, position: Int) {
        binding.setVariable(BR.license, item)
    }

}