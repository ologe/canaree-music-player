package dev.olog.presentation.thanks

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil

@Stable
data class SpecialThanksItem(
    val title: String,
    @DrawableRes val imageRes: Int,
) {

    companion object : DiffUtil.ItemCallback<SpecialThanksItem>() {
        override fun areItemsTheSame(
            oldItem: SpecialThanksItem,
            newItem: SpecialThanksItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: SpecialThanksItem,
            newItem: SpecialThanksItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}