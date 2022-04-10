package dev.olog.presentation.about

import android.content.res.ColorStateList
import androidx.lifecycle.Lifecycle
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.DiffCallbackDisplayableItem
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.base.adapter.setOnClickListener
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.shared.android.extensions.colorAccent
import kotlinx.android.synthetic.main.item_about.view.*


class AboutFragmentAdapter(
    lifecycle: Lifecycle,
    private val navigator: NavigatorAbout,
    private val viewModel: AboutFragmentViewModel

) : ObservableAdapter<DisplayableItem>(
    lifecycle,
    DiffCallbackDisplayableItem
) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            when (item.mediaId) {
                AboutFragmentViewModel.HAVOC_ID -> navigator.toHavocPage()
                AboutFragmentViewModel.THIRD_SW_ID -> navigator.toLicensesFragment()
                AboutFragmentViewModel.SPECIAL_THANKS_ID -> navigator.toSpecialThanksFragment()
                AboutFragmentViewModel.RATE_ID -> navigator.toMarket()
                AboutFragmentViewModel.PRIVACY_POLICY -> navigator.toPrivacyPolicy()
                AboutFragmentViewModel.BUY_PRO -> viewModel.buyPro()
                AboutFragmentViewModel.COMMUNITY -> navigator.joinCommunity()
                AboutFragmentViewModel.BETA -> navigator.joinBeta()
                AboutFragmentViewModel.CHANGELOG -> navigator.toChangelog()
                AboutFragmentViewModel.GITHUB -> navigator.toGithub()
                AboutFragmentViewModel.TRANSLATION -> navigator.toTranslations()
            }
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        require(item is DisplayableHeader)
        holder.itemView.apply {
            if (item.mediaId == AboutFragmentViewModel.BUY_PRO) {
                title.setTextColor(ColorStateList.valueOf(context.colorAccent()))
            }
            title.text = item.title
            subtitle.text = item.subtitle   
        }
    }

}