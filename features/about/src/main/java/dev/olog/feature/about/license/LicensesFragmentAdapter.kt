package dev.olog.feature.about.license

import android.text.method.LinkMovementMethod
import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.about.R
import dev.olog.feature.base.adapter.LayoutContainerViewHolder
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.setOnClickListener
import kotlinx.android.synthetic.main.item_license.*

internal class LicensesFragmentAdapter(

) : ObservableAdapter<LicenseFragmentModel>(LicenseFragmentModelDiff) {

    override fun getItemViewType(position: Int): Int = R.layout.item_license

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { _, _, _ ->
            val maxLines = if (viewHolder.license.maxLines > 10) 10 else Int.MAX_VALUE
            viewHolder.license.maxLines = maxLines
        }
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: LicenseFragmentModel,
        position: Int
    ) = holder.bindView {
        name.text = item.name
        url.text = item.url
        url.movementMethod = LinkMovementMethod.getInstance()
        license.text = item.license
    }

}

private object LicenseFragmentModelDiff : DiffUtil.ItemCallback<LicenseFragmentModel>() {

    override fun areItemsTheSame(
        oldItem: LicenseFragmentModel,
        newItem: LicenseFragmentModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: LicenseFragmentModel,
        newItem: LicenseFragmentModel
    ): Boolean {
        return oldItem == newItem
    }
}