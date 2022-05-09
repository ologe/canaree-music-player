package dev.olog.feature.about

import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.ui.model.DiffCallbackDisplayableItem
import dev.olog.ui.model.DisplayableHeader
import dev.olog.ui.model.DisplayableItem
import kotlinx.android.synthetic.main.item_about.view.*


class AboutFragmentAdapter(
    private val onHavocClick: () -> Unit,
    private val onThirdPartyClick: () -> Unit,
    private val onSpecialThanksClick: () -> Unit,
    private val onRateClick: () -> Unit,
    private val onPrivacyPolicyClick: () -> Unit,
    private val onCommunityClick: () -> Unit,
    private val onBetaClick: () -> Unit,
    private val onChangelogClick: () -> Unit,
    private val onGithubClick: () -> Unit,
    private val onTranslationsClick: () -> Unit,
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            when (item.mediaId) {
                AboutFragmentViewModel.HAVOC_ID -> onHavocClick()
                AboutFragmentViewModel.THIRD_SW_ID -> onThirdPartyClick()
                AboutFragmentViewModel.SPECIAL_THANKS_ID -> onSpecialThanksClick()
                AboutFragmentViewModel.RATE_ID -> onRateClick()
                AboutFragmentViewModel.PRIVACY_POLICY -> onPrivacyPolicyClick()
                AboutFragmentViewModel.COMMUNITY -> onCommunityClick()
                AboutFragmentViewModel.BETA -> onBetaClick()
                AboutFragmentViewModel.CHANGELOG -> onChangelogClick()
                AboutFragmentViewModel.GITHUB -> onGithubClick()
                AboutFragmentViewModel.TRANSLATION -> onTranslationsClick()
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