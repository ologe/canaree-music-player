package dev.olog.feature.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.about.databinding.ItemAboutBinding
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
) : ListAdapter<AboutItem, AboutFragmentAdapter.ViewHolder>(IdentityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vh = ViewHolder(parent)
        vh.itemView.setOnClickListener {
            val item = getItem(vh.adapterPosition)
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ViewHolder(
        viewGroup: ViewGroup,
        private val binding: ItemAboutBinding = ItemAboutBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AboutItem) = with(binding) {
            title.text = item.title
            subtitle.text = item.subtitle
        }

    }

}