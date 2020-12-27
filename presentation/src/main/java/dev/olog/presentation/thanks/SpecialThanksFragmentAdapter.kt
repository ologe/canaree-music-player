package dev.olog.presentation.thanks

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.base.adapter.LayoutContainerViewHolder
import dev.olog.feature.base.adapter.ObservableAdapter
import dev.olog.lib.image.provider.GlideApp
import dev.olog.presentation.model.SpecialThanksModel
import kotlinx.android.synthetic.main.item_special_thanks.*

class SpecialThanksFragmentAdapter(

) : ObservableAdapter<SpecialThanksModel>(DiffUtilSpecialThansModel) {

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: SpecialThanksModel,
        position: Int
    ) = holder.bindView {
        GlideApp.with(context)
            .load(ContextCompat.getDrawable(context, item.image))
            .into(image)

        title.text = item.title
    }

}

object DiffUtilSpecialThansModel : DiffUtil.ItemCallback<SpecialThanksModel>() {
    override fun areItemsTheSame(
        oldItem: SpecialThanksModel,
        newItem: SpecialThanksModel
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: SpecialThanksModel,
        newItem: SpecialThanksModel
    ): Boolean {
        return oldItem == newItem
    }
}