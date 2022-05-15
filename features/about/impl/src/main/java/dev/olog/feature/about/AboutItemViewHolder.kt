package dev.olog.feature.about

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.about.databinding.ItemAboutBinding
import dev.olog.feature.about.databinding.ItemAboutPromotionBinding

sealed class AboutItemViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: AboutItem)

}

class AboutItemViewDefaultViewHolder(
    viewGroup: ViewGroup,
    private val binding: ItemAboutBinding = ItemAboutBinding.inflate(
        LayoutInflater.from(viewGroup.context), viewGroup, false
    )
) : AboutItemViewHolder(binding.root) {

    override fun bind(item: AboutItem) = with(binding) {
        title.text = item.title
        subtitle.text = item.subtitle
    }

}

class AboutItemViewPromotionViewHolder(
    viewGroup: ViewGroup,
    private val binding: ItemAboutPromotionBinding = ItemAboutPromotionBinding.inflate(
        LayoutInflater.from(viewGroup.context), viewGroup, false
    )
) : AboutItemViewHolder(binding.root) {

    override fun bind(item: AboutItem) = with(binding) {
        title.text = item.title
        subtitle.text = item.subtitle
    }

}