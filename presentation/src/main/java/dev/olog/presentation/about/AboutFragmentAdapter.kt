package dev.olog.presentation.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.about.AboutItemType.*
import dev.olog.feature.presentation.base.CustomListAdapter
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.setOnClickListener
import dev.olog.presentation.navigator.NavigatorAbout
import dev.olog.shared.exhaustive
import kotlinx.android.synthetic.main.item_about.view.*

class AboutFragmentAdapter(
    private val navigator: NavigatorAbout
) : CustomListAdapter<AboutItem, RecyclerView.ViewHolder>(AboutItemDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = inflater.inflate(viewType, parent, false)
        val viewHolder =
            DataBoundViewHolder(
                binding
            )
        initViewHolderListeners(viewHolder)
        return viewHolder
    }

    fun initViewHolderListeners(viewHolder: DataBoundViewHolder) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            when (item.itemType) {
                THIRD_SW_ID -> navigator.toLicensesFragment()
                SPECIAL_THANKS_ID -> navigator.toSpecialThanksFragment()
                RATE_ID -> navigator.toMarket()
                PRIVACY_POLICY -> navigator.toPrivacyPolicy()
                COMMUNITY -> navigator.joinCommunity()
                BETA -> navigator.joinBeta()
                CHANGELOG -> navigator.toChangelog()
                GITHUB -> navigator.toGithub()
                TRANSLATION -> navigator.toTranslations()
                AUTHOR_ID,
                VERSION -> {
                }
            }.exhaustive
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.apply {
            title.text = item.title
            subtitle.text = item.subtitle
        }
    }

}

private object AboutItemDiff : DiffUtil.ItemCallback<AboutItem>() {

    override fun areItemsTheSame(oldItem: AboutItem, newItem: AboutItem): Boolean {
        return oldItem.itemType == newItem.itemType
    }

    override fun areContentsTheSame(oldItem: AboutItem, newItem: AboutItem): Boolean {
        return oldItem == newItem
    }
}