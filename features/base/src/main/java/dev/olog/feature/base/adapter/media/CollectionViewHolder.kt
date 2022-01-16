package dev.olog.feature.base.adapter.media

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.base.BindingsAdapter
import dev.olog.feature.base.R
import dev.olog.feature.base.widget.QuickActionView
import dev.olog.shared.android.extensions.inflate
import dev.olog.shared.android.extensions.setSize
import dev.olog.shared.exhaustive

class CollectionViewHolder(
    viewGroup: ViewGroup,
    direction: ItemDirection = ItemDirection.Vertical,
) : RecyclerView.ViewHolder(viewGroup.inflate(R.layout.item_media_collection)) {

    private val cover = itemView.findViewById<ImageView>(R.id.cover)
    private val quickAction = itemView.findViewById<QuickActionView>(R.id.quickAction)
    private val title = itemView.findViewById<TextView>(R.id.firstText)
    private val subtitle = itemView.findViewById<TextView>(R.id.secondText)

    init {
        when (direction) {
            is ItemDirection.Vertical -> itemView.setSize(MATCH_PARENT, WRAP_CONTENT)
            is ItemDirection.Horizontal -> itemView.setSize(direction.size, WRAP_CONTENT)
        }.exhaustive
    }

    fun bind(item: MediaListItem.Collection) {
        BindingsAdapter.loadAlbumImage(cover, item.uri)
        quickAction.setId(item.uri)
        title.text = item.title
        subtitle.text = item.subtitle
    }

}