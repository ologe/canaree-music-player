package dev.olog.presentation.base.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.R
import dev.olog.shared.android.coroutine.viewScope
import kotlinx.android.extensions.LayoutContainer
import kotlinx.coroutines.CoroutineScope

class LayoutContainerViewHolder(view: View) : RecyclerView.ViewHolder(view),
    LayoutContainer {

    val context: Context
        get() = itemView.context

    val imageView: ImageView? = itemView.findViewById(R.id.cover)

    override val containerView: View = itemView

    val coroutineScope: CoroutineScope
        get() = itemView.viewScope


    // use LayoutContainerViewHolder as receiver so kotlin synthetics can cache correctly
    inline fun bindView(crossinline block: LayoutContainerViewHolder.(View) -> Unit) {
        block(itemView)
    }

}