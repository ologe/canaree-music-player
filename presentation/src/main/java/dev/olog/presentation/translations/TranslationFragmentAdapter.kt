package dev.olog.presentation.translations

import dev.olog.feature.base.adapter.LayoutContainerViewHolder
import dev.olog.feature.base.adapter.SimpleAdapter
import dev.olog.presentation.R
import dev.olog.presentation.navigator.NavigatorAbout
import kotlinx.android.synthetic.main.item_translations_contributor.*

class TranslationFragmentAdapter(
    data: MutableList<String>,
    private val navigator: NavigatorAbout
) : SimpleAdapter<String>(data) {

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        if (viewType == R.layout.item_translations_help){
            viewHolder.itemView.setOnClickListener {
                navigator.requestTranslation()
            }
        }
    }

    override fun LayoutContainerViewHolder.bind(
        item: String,
        position: Int
    ) = bindView {
        if (itemViewType == R.layout.item_translations_contributor) {
            text.text = item
        }
    }

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> R.layout.item_translations_help
        1 -> R.layout.item_translations_header
        else -> R.layout.item_translations_contributor
    }
}