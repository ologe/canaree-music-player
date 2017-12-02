package dev.olog.presentation.utils.extension

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View

fun <T> RecyclerView.ViewHolder.setOnClickListener(data: List<T>, func: (item: T, position: Int) -> Unit){
    itemView.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition], adapterPosition)
        }
    }
}

fun <T> RecyclerView.ViewHolder.setOnLongClickListener(data: List<T>, func: (item: T, position: Int) -> Unit){
    itemView.setOnLongClickListener inner@ {
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition], adapterPosition)
            return@inner true
        }
        false
    }
}

fun <T> RecyclerView.ViewHolder.setOnClickListener(@IdRes resId: Int, data: List<T>, func: (item: T, position: Int, view: View) -> Unit){
    itemView.findViewById<View>(resId)?.setOnClickListener { view ->
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition], adapterPosition, view)
        }
    }
}