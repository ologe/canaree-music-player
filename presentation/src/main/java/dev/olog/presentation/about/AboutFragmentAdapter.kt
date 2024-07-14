package dev.olog.presentation.about

import android.view.LayoutInflater
import android.view.ViewGroup
import dev.olog.core.MediaId
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
            when (item.mediaId) {
                AboutFragmentViewModel.THIRD_SW_ID -> navigator.toLicensesFragment()
                AboutFragmentViewModel.SPECIAL_THANKS_ID -> navigator.toSpecialThanksFragment()
                AboutFragmentViewModel.RATE_ID -> navigator.toMarket()
                AboutFragmentViewModel.PRIVACY_POLICY -> navigator.toPrivacyPolicy()
                AboutFragmentViewModel.COMMUNITY -> navigator.joinCommunity()
                AboutFragmentViewModel.BETA -> navigator.joinBeta()
                AboutFragmentViewModel.CHANGELOG -> navigator.toChangelog()
                AboutFragmentViewModel.GITHUB -> navigator.toGithub()
                AboutFragmentViewModel.TRANSLATION -> navigator.toTranslations()
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
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)