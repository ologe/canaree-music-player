package dev.olog.presentation.license

import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil

@Stable
data class LicenseFragmentItem(
    val name: String,
    val url: String,
    val license: String,
) {

    companion object : DiffUtil.ItemCallback<LicenseFragmentItem>() {

        override fun areItemsTheSame(
            oldItem: LicenseFragmentItem,
            newItem: LicenseFragmentItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: LicenseFragmentItem,
            newItem: LicenseFragmentItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}