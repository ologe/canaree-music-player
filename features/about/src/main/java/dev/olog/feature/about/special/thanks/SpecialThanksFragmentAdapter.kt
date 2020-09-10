package dev.olog.feature.about.special.thanks

import androidx.core.content.ContextCompat
import dev.olog.feature.about.model.SpecialThanksModel
import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.adapter.ObservableAdapter
import dev.olog.lib.image.loader.GlideApp

internal class SpecialThanksFragmentAdapter(
) : ObservableAdapter<SpecialThanksModel>(DiffUtilSpecialThansModel) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
    }

    override fun bind(holder: DataBoundViewHolder, item: SpecialThanksModel, position: Int) {
        holder.itemView.apply {
//            GlideApp.with(context)
//                .load(ContextCompat.getDrawable(context, item.image))
//                .into(image)
//
//            title.text = item.title
        }
    }

}