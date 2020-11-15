package dev.olog.presentation.base.adapter

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.R
import dev.olog.shared.android.coroutine.viewScope
import kotlinx.android.extensions.LayoutContainer
import kotlinx.coroutines.CoroutineScope

class DataBoundViewHolder(view: View) : RecyclerView.ViewHolder(view),
    LayoutContainer {

    val imageView: ImageView? = itemView.findViewById(R.id.cover)

    override val containerView: View = itemView

    val coroutineScope: CoroutineScope
        get() = itemView.viewScope


}