package dev.olog.presentation.utils.extension

import android.support.v7.widget.RecyclerView

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