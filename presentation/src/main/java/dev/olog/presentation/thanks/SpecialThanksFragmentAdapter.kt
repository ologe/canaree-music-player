package dev.olog.presentation.thanks

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import dev.olog.image.provider.GlideApp
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.ObservableAdapter
import dev.olog.presentation.model.SpecialThanksModel
import kotlinx.android.synthetic.main.item_special_thanks.view.*

class SpecialThanksFragmentAdapter(
) : ObservableAdapter<SpecialThanksModel>(DiffUtilSpecialThansModel) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
    }

    override fun bind(holder: DataBoundViewHolder, item: SpecialThanksModel, position: Int) {
        holder.itemView.apply {
            GlideApp.with(context)
                .load(ContextCompat.getDrawable(context, item.image))
                .into(image)

            title.text = item.title
        }
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