package dev.olog.presentation.fragment_licenses

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.text.method.LinkMovementMethod
import dev.olog.presentation.BR
import dev.olog.presentation._base.BaseListAdapter
import dev.olog.presentation._base.DataBoundViewHolder
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

    override fun getItemViewType(position: Int): Int = dataController[position].type

    override fun areItemsTheSame(oldItem: LicenseModel, newItem: LicenseModel): Boolean {
        return oldItem.url == newItem.url
    }
}