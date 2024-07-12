package dev.olog.presentation.license

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import dev.olog.presentation.base.adapter.CustomListAdapter
import dev.olog.presentation.base.adapter.CustomViewHolder
import dev.olog.presentation.base.adapter.setOnClickListener
import dev.olog.presentation.databinding.ItemLicenseBinding

class LicensesFragmentAdapter(

) : CustomListAdapter<LicenceItem, ItemLicenseBinding>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): ItemLicenseBinding {
        return ItemLicenseBinding.inflate(inflater, parent, false)
    }

    override fun initViewHolderListeners(
        viewHolder: CustomViewHolder<ItemLicenseBinding>,
        viewType: Int
    ) {
        viewHolder.setOnClickListener(this) { _, _, _ ->
            val maxLines = if (viewHolder.binding.license.maxLines > 10) 10 else Int.MAX_VALUE
            viewHolder.binding.license.maxLines = maxLines
        }
    }

    override fun bind(
        holder: CustomViewHolder<ItemLicenseBinding>,
        item: LicenceItem,
        position: Int
    ) {
        holder.binding.apply {
            name.text = item.name
            url.text = item.url
            url.movementMethod = LinkMovementMethod.getInstance()
            license.text = item.license

        }
    }

}

data class LicenceItem(
    val name: String,
    val url: String,
    val license: String,
)