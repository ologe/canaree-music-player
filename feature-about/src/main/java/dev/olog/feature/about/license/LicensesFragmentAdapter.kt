package dev.olog.feature.about.license

import android.text.method.LinkMovementMethod
import dev.olog.feature.about.model.LicenseModel
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.adapter.ObservableAdapter
import dev.olog.feature.presentation.base.adapter.setOnClickListener
import kotlinx.android.synthetic.main.item_license.view.*

internal class LicensesFragmentAdapter(

) : ObservableAdapter<LicenseModel>(DiffCallbackLicenseModel) {

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

