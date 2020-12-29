package dev.olog.feature.about.special.thanks

import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.about.R
import dev.olog.feature.base.adapter.LayoutContainerViewHolder
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.lib.image.provider.GlideApp
import kotlinx.android.synthetic.main.item_special_thanks.*

internal class SpecialThanksFragmentAdapter(

) : ObservableAdapter<SpecialThanksFragmentModel>(SpecialThanksFragmentModelDiff) {

    override fun getItemViewType(position: Int): Int = R.layout.item_special_thanks

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: SpecialThanksFragmentModel,
        position: Int
    ) = holder.bindView {
        GlideApp.with(context) // TODO normalize icon size
            .load(item.drawable)
            .into(image)

        title.text = item.title
    }

}

private object SpecialThanksFragmentModelDiff : DiffUtil.ItemCallback<SpecialThanksFragmentModel>() {

    override fun areItemsTheSame(
        oldItem: SpecialThanksFragmentModel,
        newItem: SpecialThanksFragmentModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: SpecialThanksFragmentModel,
        newItem: SpecialThanksFragmentModel
    ): Boolean {
        return oldItem == newItem
    }
}