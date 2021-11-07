package dev.olog.feature.about

import androidx.lifecycle.Lifecycle
import dev.olog.feature.about.navigator.NavigatorAbout
import dev.olog.feature.base.adapter.DataBoundViewHolder
import dev.olog.feature.base.adapter.DiffCallbackDisplayableItem
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.setOnClickListener
import dev.olog.feature.base.model.DisplayableHeader
import dev.olog.feature.base.model.DisplayableItem
import kotlinx.android.synthetic.main.item_about.view.*


class AboutFragmentAdapter(
    lifecycle: Lifecycle,
    private val navigator: NavigatorAbout,

) : ObservableAdapter<DisplayableItem>(
    lifecycle,
    DiffCallbackDisplayableItem
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
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

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        require(item is DisplayableHeader)
        holder.itemView.apply {
            title.text = item.title
            subtitle.text = item.subtitle   
        }
    }

}