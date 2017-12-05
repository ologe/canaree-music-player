package dev.olog.presentation.utils.extension

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View
import dev.olog.presentation._base.BaseListAdapterController
import dev.olog.presentation._base.BaseMapAdapterController

fun <T> RecyclerView.ViewHolder.setOnClickListener(data: BaseListAdapterController<T>, func: (item: T, position: Int) -> Unit){
    itemView.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition], adapterPosition)
        }
    }
}

fun <T> RecyclerView.ViewHolder.setOnClickListener(data: BaseMapAdapterController<*,T>, func: (item: T, position: Int) -> Unit){
    itemView.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition], adapterPosition)
        }
    }
}

fun <T> RecyclerView.ViewHolder.setOnLongClickListener(data: BaseListAdapterController<T>, func: (item: T, position: Int) -> Unit){
    itemView.setOnLongClickListener inner@ {
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition], adapterPosition)
            return@inner true
        }
        false
    }
}

fun <T> RecyclerView.ViewHolder.setOnLongClickListener(data: BaseMapAdapterController<*, T>, func: (item: T, position: Int) -> Unit){
    itemView.setOnLongClickListener inner@ {
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition], adapterPosition)
            return@inner true
        }
        false
    }
}

fun <T> RecyclerView.ViewHolder.setOnClickListener(@IdRes resId: Int, data: BaseListAdapterController<T>, func: (item: T, position: Int, view: View) -> Unit){
    itemView.findViewById<View>(resId)?.setOnClickListener { view ->
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition], adapterPosition, view)
        }
    }
}

fun <T> RecyclerView.ViewHolder.setOnClickListener(@IdRes resId: Int, data: BaseMapAdapterController<*,T>, func: (item: T, position: Int, view: View) -> Unit){
    itemView.findViewById<View>(resId)?.setOnClickListener { view ->
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition], adapterPosition, view)
        }
    }
}