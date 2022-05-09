package dev.olog.feature.about.thanks

import androidx.core.content.ContextCompat
import dev.olog.image.provider.GlideApp
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
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