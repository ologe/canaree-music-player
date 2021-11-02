package dev.olog.presentation.license

import android.text.method.LinkMovementMethod
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.base.adapter.DataBoundViewHolder
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.setOnClickListener
import dev.olog.presentation.model.LicenseModel
import kotlinx.android.synthetic.main.item_license.view.*

class LicensesFragmentAdapter(
    lifecycle: Lifecycle
) : ObservableAdapter<LicenseModel>(lifecycle,
    DiffCallbackLicenseModel
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { _, _, _ ->
            val maxLines = if (viewHolder.itemView.license.maxLines > 10) 10 else Int.MAX_VALUE
            viewHolder.itemView.license.maxLines = maxLines
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: LicenseModel, position: Int) {
        holder.itemView.apply {
            name.text = item.name
            url.text = item.url
            url.movementMethod = LinkMovementMethod.getInstance()
            license.text = item.license

        }
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