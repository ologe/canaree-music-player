package dev.olog.feature.about.localization

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.about.R
import dev.olog.feature.base.adapter.LayoutContainerViewHolder
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.navigation.Navigator
import dev.olog.shared.exhaustive
import kotlinx.android.synthetic.main.item_translations_contributor.*

internal class LocalizationFragmentAdapter(
    private val navigator: Navigator
) : ObservableAdapter<LocalizationFragmentModel>(LocalizationFragmentModelDiff) {

    override fun getItemViewType(position: Int): Int = getItem(position).layoutType

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        if (viewType == R.layout.item_translations_help){
            viewHolder.itemView.setOnClickListener {
                navigator.requestTranslation()
            }
        }
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: LocalizationFragmentModel,
        position: Int
    ) = holder.bindView {
        when (item) {
            LocalizationFragmentModel.Help,
            LocalizationFragmentModel.Header -> {}
            is LocalizationFragmentModel.Contributor -> text.text = item.name
        }.exhaustive
    }

}

private object LocalizationFragmentModelDiff : DiffUtil.ItemCallback<LocalizationFragmentModel>() {

    override fun areItemsTheSame(
        oldItem: LocalizationFragmentModel,
        newItem: LocalizationFragmentModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: LocalizationFragmentModel,
        newItem: LocalizationFragmentModel
    ): Boolean {
        return oldItem == newItem
    }
}