package dev.olog.msc.presentation.licenses

import android.text.method.LinkMovementMethod
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.msc.BR
import dev.olog.msc.databinding.ItemLicenseBinding
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.utils.k.extension.setOnClickListener
import kotlinx.android.synthetic.main.item_license.view.*

class LicensesFragmentAdapter (lifecycle: Lifecycle)
    : AbsAdapter<LicenseModel>(lifecycle){

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        (viewHolder.binding as ItemLicenseBinding)
                .url.movementMethod = LinkMovementMethod.getInstance()

        viewHolder.setOnClickListener(controller) { _, _, _ ->
            val maxLines = if (viewHolder.itemView.license.maxLines > 10) 10 else Int.MAX_VALUE
            viewHolder.itemView.license.maxLines = maxLines
        }
    }

    override fun bind(binding: ViewDataBinding, item: LicenseModel, position: Int) {
        binding.setVariable(BR.licenseModel, item)
    }

}