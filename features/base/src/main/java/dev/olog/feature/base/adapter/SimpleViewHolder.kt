package dev.olog.feature.base.adapter

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.base.R
import dev.olog.shared.lazyFast

class SimpleViewHolder(
    view: View,
    init: RecyclerView.ViewHolder.() -> Unit
): RecyclerView.ViewHolder(view) {

    val imageView : ImageView? by lazyFast { itemView.findViewById<ImageView>(R.id.cover) }

    init {
        init(this)
    }

}