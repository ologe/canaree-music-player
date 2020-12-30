package dev.olog.feature.about.about

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.about.R
import dev.olog.feature.base.adapter.LayoutContainerViewHolder
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.feature.base.adapter.setOnClickListener
import dev.olog.navigation.Navigator
import dev.olog.shared.exhaustive
import kotlinx.android.synthetic.main.item_about.*


internal class AboutFragmentAdapter(
    private val navigator: Navigator
) : ObservableAdapter<AboutFragmentModel>(AboutFragmentModelDiff) {

    override fun getItemViewType(position: Int): Int = R.layout.item_about

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            when (item.type) {
                AboutFragmentType.AUTHOR -> {}
                AboutFragmentType.THIRD_SOFTWARE -> navigator.toLicensesFragment()
                AboutFragmentType.SPECIAL_THANKS -> navigator.toSpecialThanksFragment()
                AboutFragmentType.PRIVACY_POLICY -> navigator.toPrivacyPolicy()
                AboutFragmentType.COMMUNITY -> navigator.joinCommunity()
                AboutFragmentType.BETA -> navigator.joinBeta()
                AboutFragmentType.CHANGELOG -> navigator.toChangelog()
                AboutFragmentType.GITHUB -> navigator.toGithub()
                AboutFragmentType.LOCALIZATION -> navigator.toLocalization()
            }.exhaustive
        }
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: AboutFragmentModel,
        position: Int
    ) = holder.bindView {
        title.text = item.title
        subtitle.text = item.subtitle
    }

}

private object AboutFragmentModelDiff : DiffUtil.ItemCallback<AboutFragmentModel>() {
    override fun areItemsTheSame(
        oldItem: AboutFragmentModel,
        newItem: AboutFragmentModel
    ): Boolean {
        return oldItem.type == newItem.type
    }

    override fun areContentsTheSame(
        oldItem: AboutFragmentModel,
        newItem: AboutFragmentModel
    ): Boolean {
        return oldItem == newItem
    }
}