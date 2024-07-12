package dev.olog.presentation.base.adapter

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

fun <T : Any> RecyclerView.ViewHolder.setOnClickListener(
    data: CustomListAdapter<T, *>,
    func: (item: T, position: Int, view: View) -> Unit
) {

    this.itemView.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            func(data.getItem(adapterPosition), adapterPosition, it)
        }
    }
}

fun <T : Any> RecyclerView.ViewHolder.setOnClickListener(
    @IdRes resId: Int,
    data: CustomListAdapter<T, *>,
    func: (item: T, position: Int, view: View) -> Unit
) {

    this.itemView.findViewById<View>(resId)?.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            func(data.getItem(adapterPosition), adapterPosition, it)
        }
    }
}

fun <T : Any> RecyclerView.ViewHolder.setOnLongClickListener(
    data: CustomListAdapter<T, *>,
    func: (item: T, position: Int, view: View) -> Unit
) {

    itemView.setOnLongClickListener inner@{
        if (adapterPosition != RecyclerView.NO_POSITION) {
            func(data.getItem(adapterPosition), adapterPosition, it)
            return@inner true
        }
        false
    }
}