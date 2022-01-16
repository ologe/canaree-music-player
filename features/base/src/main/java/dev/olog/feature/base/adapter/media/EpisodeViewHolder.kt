package dev.olog.feature.base.adapter.media

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.base.BindingsAdapter
import dev.olog.feature.base.R
import dev.olog.shared.android.extensions.inflate

class EpisodeViewHolder(
    viewGroup: ViewGroup
) : RecyclerView.ViewHolder(viewGroup.inflate(R.layout.item_media_episode)) {

    private val cover = itemView.findViewById<ImageView>(R.id.cover)
    private val title = itemView.findViewById<TextView>(R.id.firstText)
    private val subtitle = itemView.findViewById<TextView>(R.id.secondText)
    private val duration = itemView.findViewById<TextView>(R.id.duration)

    fun bind(item: MediaListItem.Track) {
        BindingsAdapter.loadSongImage(cover, item.uri)
        title.text = item.title
        subtitle.text = item.subtitle
        duration.text = item.readableDuration
    }

}