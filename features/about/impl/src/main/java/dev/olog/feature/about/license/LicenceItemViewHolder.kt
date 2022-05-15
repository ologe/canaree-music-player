package dev.olog.feature.about.license

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.about.databinding.ItemLicenseBinding

class LicenceItemViewHolder(
    viewGroup: ViewGroup,
    private val binding: ItemLicenseBinding = ItemLicenseBinding.inflate(
        LayoutInflater.from(viewGroup.context), viewGroup, false
    )
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.url.movementMethod = LinkMovementMethod.getInstance()

        binding.license.setOnClickListener {
            toggleExpansion()
        }
    }

    private fun toggleExpansion() = with(binding) {
        val maxLines = if (license.maxLines > 10) 10 else Int.MAX_VALUE
        license.maxLines = maxLines
    }

    fun bind(item: LicenseItem) = with(binding) {
        name.text = item.name
        url.text = item.url
        license.text = item.license
    }

}