package dev.olog.feature.about.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.extensions.findActivity
import dev.olog.feature.about.NavigatorAbout
import dev.olog.feature.about.model.AboutItem
import dev.olog.feature.about.model.AboutItemType.*
import dev.olog.feature.presentation.base.CustomListAdapter
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.adapter.setOnClickListener
import dev.olog.shared.exhaustive

internal class AboutFragmentAdapter(
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

    private fun initViewHolderListeners(viewHolder: DataBoundViewHolder) {
        viewHolder.setOnClickListener(this) { item, _, view ->
            when (item.itemType) {
                THIRD_SW_ID -> navigator.toLicensesFragment(view.findActivity())
                SPECIAL_THANKS_ID -> navigator.toSpecialThanksFragment(view.findActivity())
                RATE_ID -> navigator.toMarket(view.findActivity())
                PRIVACY_POLICY -> navigator.toPrivacyPolicy(view.findActivity())
                COMMUNITY -> navigator.joinCommunity(view.findActivity())
                BETA -> navigator.joinBeta(view.findActivity())
                CHANGELOG -> navigator.toChangelog(view.findActivity())
                GITHUB -> navigator.toGithub(view.findActivity())
                TRANSLATION -> navigator.toTranslations(view.findActivity())
                AUTHOR_ID,
                VERSION -> {
                }
            }.exhaustive
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.apply {
//            title.text = item.title
//            subtitle.text = item.subtitle
        }
    }

}

