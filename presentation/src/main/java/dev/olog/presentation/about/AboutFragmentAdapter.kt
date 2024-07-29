package dev.olog.presentation.about

import android.view.LayoutInflater
import android.view.ViewGroup
import dev.olog.presentation.about.AboutFragmentViewModel.*
import dev.olog.presentation.base.adapter.CustomListAdapter
import dev.olog.presentation.base.adapter.CustomViewHolder
import dev.olog.presentation.base.adapter.setOnClickListener
import dev.olog.presentation.databinding.ItemAboutBinding
import dev.olog.presentation.navigator.NavigatorAbout


class AboutFragmentAdapter(
    private val navigator: NavigatorAbout,
) : CustomListAdapter<AboutItem, ItemAboutBinding>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): ItemAboutBinding {
        return ItemAboutBinding.inflate(inflater, parent, false)
    }

    override fun initViewHolderListeners(
        viewHolder: CustomViewHolder<ItemAboutBinding>,
        viewType: Int
    ) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            when (item.type) {
                AboutType.THIRD_SW_ID -> navigator.toLicensesFragment()
                AboutType.SPECIAL_THANKS_ID -> navigator.toSpecialThanksFragment()
                AboutType.RATE_ID -> navigator.toMarket()
                AboutType.PRIVACY_POLICY -> navigator.toPrivacyPolicy()
                AboutType.COMMUNITY -> navigator.joinCommunity()
                AboutType.BETA -> navigator.joinBeta()
                AboutType.CHANGELOG -> navigator.toChangelog()
                AboutType.GITHUB -> navigator.toGithub()
                AboutType.TRANSLATION -> navigator.toTranslations()
                AboutType.AUTHOR_ID,
                AboutType.VERSION -> {}
            }
        }
    }

    override fun bind(
        holder: CustomViewHolder<ItemAboutBinding>,
        item: AboutItem,
        position: Int
    ) {
        holder.binding.apply {
            title.text = item.title
            subtitle.text = item.subtitle   
        }
    }

}

data class AboutItem(
    val type: AboutType,
    val title: String,
    val subtitle: String,
)