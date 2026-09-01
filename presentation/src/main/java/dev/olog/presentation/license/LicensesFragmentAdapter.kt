package dev.olog.presentation.license

import android.text.method.LinkMovementMethod
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.base.adapter.setOnClickListener
import dev.olog.presentation.databinding.ItemLicenseBinding
import dev.olog.presentation.model.LicenseModel

class LicensesFragmentAdapter(
    lifecycle: Lifecycle
) : ObservableAdapter<LicenseModel>(lifecycle,
    DiffCallbackLicenseModel
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { _, _, _ ->
            val binding = ItemLicenseBinding.bind(viewHolder.itemView)
            val maxLines = if (binding.license.maxLines > 10) 10 else Int.MAX_VALUE
            binding.license.maxLines = maxLines
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: LicenseModel, position: Int) {
        val binding = ItemLicenseBinding.bind(holder.itemView)
        binding.name.text = item.name
        binding.url.text = item.url
        binding.url.movementMethod = LinkMovementMethod.getInstance()
        binding.license.text = item.license
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