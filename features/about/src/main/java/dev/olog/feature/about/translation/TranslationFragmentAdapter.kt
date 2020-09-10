package dev.olog.feature.about.translation

import dev.olog.core.extensions.findActivity
import dev.olog.feature.about.NavigatorAbout
import dev.olog.feature.about.R
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.adapter.SimpleAdapter

internal class TranslationFragmentAdapter(
    private val navigator: NavigatorAbout
) : SimpleAdapter<String>() {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        if (viewType == R.layout.item_translations_help){
            viewHolder.itemView.setOnClickListener {
                navigator.requestTranslation(it.findActivity())
            }
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: String, position: Int) {
        if (holder.itemViewType == R.layout.item_translations_contributor) {
//            holder.itemView.text.text = item
        }
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> R.layout.item_translations_help
        1 -> R.layout.item_translations_header
        else -> R.layout.item_translations_contributor
    }
}