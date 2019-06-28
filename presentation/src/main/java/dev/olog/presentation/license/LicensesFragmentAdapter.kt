package dev.olog.presentation.license

import android.text.method.LinkMovementMethod
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import dev.olog.presentation.BR
import dev.olog.presentation.base.DataBoundViewHolder
import dev.olog.presentation.base.ObservableAdapter
import dev.olog.presentation.base.setOnClickListener
import dev.olog.presentation.databinding.ItemLicenseBinding
import dev.olog.presentation.model.LicenseModel
import kotlinx.android.synthetic.main.item_license.view.*

class LicensesFragmentAdapter(
    lifecycle: Lifecycle
) : ObservableAdapter<LicenseModel>(lifecycle,
    DiffCallbackLicenseModel
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        (viewHolder.binding as ItemLicenseBinding)
            .url.movementMethod = LinkMovementMethod.getInstance()

        viewHolder.setOnClickListener(this) { _, _, _ ->
            val maxLines = if (viewHolder.itemView.license.maxLines > 10) 10 else Int.MAX_VALUE
            viewHolder.itemView.license.maxLines = maxLines
        }
    }

    override fun bind(binding: ViewDataBinding, item: LicenseModel, position: Int) {
        binding.setVariable(BR.licenseModel, item)
    }

}

object DiffCallbackLicenseModel : DiffUtil.ItemCallback<LicenseModel>() {
    override fun areItemsTheSame(oldItem: LicenseModel, newItem: LicenseModel): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: LicenseModel, newItem: LicenseModel): Boolean {
        return oldItem == newItem
    }
}