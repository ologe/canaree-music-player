package dev.olog.presentation.license

import android.text.method.LinkMovementMethod
import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.base.adapter.LayoutContainerViewHolder
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.setOnClickListener
import dev.olog.presentation.model.LicenseModel
import kotlinx.android.synthetic.main.item_license.*

class LicensesFragmentAdapter(

) : ObservableAdapter<LicenseModel>(DiffCallbackLicenseModel) {

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { _, _, _ ->
            val maxLines = if (viewHolder.license.maxLines > 10) 10 else Int.MAX_VALUE
            viewHolder.license.maxLines = maxLines
        }
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: LicenseModel,
        position: Int
    ) = holder.bindView {
        name.text = item.name
        url.text = item.url
        url.movementMethod = LinkMovementMethod.getInstance()
        license.text = item.license
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