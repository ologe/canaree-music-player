package dev.olog.presentation.about

import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil

@Stable
data class AboutFragmentItem(
    val type: Type,
    val title: String,
    val subtitle: String,
) {

    enum class Type {
        Author,
        Version,
        Community,
        Beta,
        Rate,
        SpecialThanks,
        Translations,
        ChangeLog,
        Github,
        ThirdPartySoftware,
        PrivacyPolicy, // TODO update link?
    }

    companion object : DiffUtil.ItemCallback<AboutFragmentItem>() {
        override fun areItemsTheSame(
            oldItem: AboutFragmentItem,
            newItem: AboutFragmentItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AboutFragmentItem,
            newItem: AboutFragmentItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}