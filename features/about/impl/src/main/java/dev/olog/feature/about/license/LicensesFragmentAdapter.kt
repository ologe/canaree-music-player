package dev.olog.feature.about.license

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.olog.platform.adapter.IdentityDiffCallback

class LicensesFragmentAdapter(

) : ListAdapter<LicenseItem, LicenceItemViewHolder>(IdentityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LicenceItemViewHolder {
        return LicenceItemViewHolder(parent)
    }

    override fun onBindViewHolder(holder: LicenceItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}