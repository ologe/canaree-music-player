package dev.olog.presentation.about

import dev.olog.presentation.base.adapter.DiffCallbackDisplayableItem
import dev.olog.presentation.base.adapter.LayoutContainerViewHolder
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.base.adapter.setOnClickListener
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.NavigatorAbout
import kotlinx.android.synthetic.main.item_about.*


class AboutFragmentAdapter(
    private val navigator: NavigatorAbout
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            when (item.mediaId) {
                AboutFragmentPresenter.THIRD_SW_ID -> navigator.toLicensesFragment()
                AboutFragmentPresenter.SPECIAL_THANKS_ID -> navigator.toSpecialThanksFragment()
                AboutFragmentPresenter.RATE_ID -> navigator.toMarket()
                AboutFragmentPresenter.PRIVACY_POLICY -> navigator.toPrivacyPolicy()
                AboutFragmentPresenter.COMMUNITY -> navigator.joinCommunity()
                AboutFragmentPresenter.BETA -> navigator.joinBeta()
                AboutFragmentPresenter.CHANGELOG -> navigator.toChangelog()
                AboutFragmentPresenter.GITHUB -> navigator.toGithub()
                AboutFragmentPresenter.TRANSLATION -> navigator.toTranslations()
            }
        }
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: DisplayableItem,
        position: Int
    ) = holder.bindView {
        require(item is DisplayableHeader)

        title.text = item.title
        subtitle.text = item.subtitle
    }

}