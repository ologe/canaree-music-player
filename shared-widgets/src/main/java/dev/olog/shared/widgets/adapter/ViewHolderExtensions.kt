package dev.olog.shared.widgets.adapter

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import dev.olog.shared.widgets.adapter.drag.IDragListener
import dev.olog.shared.widgets.animation.ScaleInOnTouch
import dev.olog.shared.widgets.animation.ScaleMoreInOnTouch

fun <T> RecyclerView.ViewHolder.setOnClickListener(
    data: ObservableAdapter<T>,
    func: (item: T, position: Int, view: View) -> Unit
) {

    this.itemView.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            func(data.getItem(adapterPosition), adapterPosition, it)
        }
    }
}

fun <T> RecyclerView.ViewHolder.setOnClickListener(
    @IdRes resId: Int,
    data: ObservableAdapter<T>,
    func: (item: T, position: Int, view: View) -> Unit
) {

    this.itemView.findViewById<View>(resId)?.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            func(data.getItem(adapterPosition), adapterPosition, it)
        }
    }
}

fun <T> RecyclerView.ViewHolder.setOnLongClickListener(
    data: ObservableAdapter<T>,
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

fun RecyclerView.ViewHolder.elevateAlbumOnTouch() {
    itemView.setOnTouchListener(ScaleMoreInOnTouch(itemView))
}

fun RecyclerView.ViewHolder.elevateSongOnTouch() {
    val viewToAnimate = itemView
    itemView.setOnTouchListener(ScaleInOnTouch(viewToAnimate))
}

@SuppressLint("ClickableViewAccessibility")
fun RecyclerView.ViewHolder.setOnDragListener(
    @IdRes dragHandleId: Int,
    dragListener: IDragListener
) {
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