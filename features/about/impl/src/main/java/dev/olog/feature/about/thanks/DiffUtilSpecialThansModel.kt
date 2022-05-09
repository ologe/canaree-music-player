package dev.olog.feature.about.thanks

import androidx.recyclerview.widget.DiffUtil

object DiffUtilSpecialThansModel : DiffUtil.ItemCallback<SpecialThanksModel>() {
    override fun areItemsTheSame(
        oldItem: SpecialThanksModel,
        newItem: SpecialThanksModel
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: SpecialThanksModel,
        newItem: SpecialThanksModel
    ): Boolean {
        return oldItem == newItem
    }
}