package dev.olog.feature.about.license

import androidx.recyclerview.widget.DiffUtil

object DiffCallbackLicenseModel : DiffUtil.ItemCallback<LicenseModel>() {
    override fun areItemsTheSame(oldItem: LicenseModel, newItem: LicenseModel): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: LicenseModel, newItem: LicenseModel): Boolean {
        return oldItem == newItem
    }
}