package dev.olog.feature.about.thanks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.about.databinding.ItemSpecialThanksBinding
import dev.olog.image.provider.GlideApp
import dev.olog.platform.adapter.IdentityDiffCallback

class SpecialThanksFragmentAdapter(

) : ListAdapter<SpecialThanksItem, SpecialThanksFragmentAdapter.ViewHolder>(IdentityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        viewGroup: ViewGroup,
        private val binding: ItemSpecialThanksBinding = ItemSpecialThanksBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SpecialThanksItem) = with(binding) {
            GlideApp.with(root.context)
                .load(ContextCompat.getDrawable(root.context, item.image))
                .into(image)

            title.text = item.title
        }

    }

}