package dev.olog.presentation.thanks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import dev.olog.image.provider.GlideApp
import dev.olog.presentation.base.adapter.CustomListAdapter
import dev.olog.presentation.base.adapter.CustomViewHolder
import dev.olog.presentation.databinding.ItemSpecialThanksBinding

class SpecialThanksFragmentAdapter(

) : CustomListAdapter<SpecialThanksItem, ItemSpecialThanksBinding>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): ItemSpecialThanksBinding {
        return ItemSpecialThanksBinding.inflate(inflater, parent, false)
    }

    override fun initViewHolderListeners(
        viewHolder: CustomViewHolder<ItemSpecialThanksBinding>,
        viewType: Int
    ) {}

    override fun bind(
        holder: CustomViewHolder<ItemSpecialThanksBinding>,
        item: SpecialThanksItem, position: Int
    ) {
        val context = holder.binding.root.context
        holder.binding.apply {
            GlideApp.with(context)
                .load(ContextCompat.getDrawable(context, item.image))
                .into(image)

            title.text = item.title
        }
    }

}

data class SpecialThanksItem(
    val title: String,
    val image: Int
)