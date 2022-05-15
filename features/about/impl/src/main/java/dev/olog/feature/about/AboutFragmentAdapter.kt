package dev.olog.feature.about

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.olog.platform.adapter.IdentityDiffCallback
import dev.olog.shared.extension.exhaustive


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
) : ListAdapter<AboutItem, AboutItemViewHolder>(IdentityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutItemViewHolder {
        val vh : AboutItemViewHolder = when (viewType) {
            R.layout.item_about -> AboutItemViewDefaultViewHolder(parent)
            R.layout.item_about_promotion -> AboutItemViewPromotionViewHolder(parent)
            else -> error("invalid viewType $viewType")
        }

        vh.itemView.setOnClickListener {
            val item = getItem(vh.bindingAdapterPosition)
            when (item.type) {
                AboutItem.Type.Havoc -> onHavocClick()
                AboutItem.Type.Licence -> onThirdPartyClick()
                AboutItem.Type.SpecialThanks -> onSpecialThanksClick()
                AboutItem.Type.Rate -> onRateClick()
                AboutItem.Type.PrivacyPolicy -> onPrivacyPolicyClick()
                AboutItem.Type.Community -> onCommunityClick()
                AboutItem.Type.Beta -> onBetaClick()
                AboutItem.Type.Changelog -> onChangelogClick()
                AboutItem.Type.Repo -> onGithubClick()
                AboutItem.Type.Translation -> onTranslationsClick()
                AboutItem.Type.Author,
                AboutItem.Type.Version -> {}
            }.exhaustive
        }
        return vh
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        if (item.type == AboutItem.Type.Havoc) {
            return R.layout.item_about_promotion
        }
        return R.layout.item_about
    }

    override fun onBindViewHolder(holder: AboutItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }



}