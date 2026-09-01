package dev.olog.presentation.translations

import android.widget.TextView
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.SimpleAdapter
import dev.olog.presentation.navigator.NavigatorAbout

class TranslationFragmentAdapter(
    data: MutableList<String>,
    private val navigator: NavigatorAbout
) : SimpleAdapter<String>(data) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        if (viewType == R.layout.item_translations_help){
            viewHolder.itemView.setOnClickListener {
                navigator.requestTranslation()
            }
        }
    }

    override fun bind(holder: DataBoundViewHolder, item: String, position: Int) {
        if (holder.itemViewType == R.layout.item_translations_contributor) {
            holder.itemView.findViewById<TextView>(R.id.text).text = item
        }
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> R.layout.item_translations_help
        1 -> R.layout.item_translations_header
        else -> R.layout.item_translations_contributor
    }
}