package dev.olog.feature.base.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.base.R
import dev.olog.shared.android.extensions.inflate
import dev.olog.shared.widgets.DottedSeparator

class TextHeaderAdapter(
    private val title: String,
    private val showSeparator: Boolean = true,
) : OptionalAdapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh = object : RecyclerView.ViewHolder(parent.inflate(R.layout.item_media_header)) {}
        vh.itemView.findViewById<TextView>(R.id.title).text = title
        vh.itemView.findViewById<DottedSeparator>(R.id.divider).isVisible = showSeparator
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun toString(): String {
        return super.toString() + "[title=$title]"
    }

}