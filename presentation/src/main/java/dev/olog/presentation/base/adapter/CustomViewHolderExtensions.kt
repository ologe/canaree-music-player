package dev.olog.presentation.base.adapter

import android.view.MotionEvent
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.base.drag.IDragListener

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

fun RecyclerView.ViewHolder.setOnDragListener(dragHandleId: Int, dragListener: IDragListener) {
    itemView.findViewById<View>(dragHandleId)?.setOnTouchListener { _, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dragListener.onStartDrag(this)
                true
            }
            else -> false
        }
    }
}