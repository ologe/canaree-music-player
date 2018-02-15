package dev.olog.msc.utils.k.extension

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.adapter.AdapterController
import dev.olog.msc.presentation.utils.ElevateAlbumOnTouch
import dev.olog.msc.presentation.utils.ElevateSongOnTouch

fun <T> RecyclerView.ViewHolder.setOnClickListener(data: AdapterController<*,T>, func: (item: T, position: Int) -> Unit){
    itemView.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition], adapterPosition)
        }
    }
}

fun <T> RecyclerView.ViewHolder.setOnLongClickListener(data: AdapterController<*, T>, func: (item: T, position: Int) -> Unit){
    itemView.setOnLongClickListener inner@ {
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition], adapterPosition)
            return@inner true
        }
        false
    }
}

fun <T> RecyclerView.ViewHolder.setOnClickListener(@IdRes resId: Int, data: AdapterController<*, T>, func: (item: T, position: Int, view: View) -> Unit){
    itemView.findViewById<View>(resId)?.setOnClickListener { view ->
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data[adapterPosition], adapterPosition, view)
        }
    }
}

fun RecyclerView.ViewHolder.elevateAlbumOnTouch(){
    itemView.setOnTouchListener(ElevateAlbumOnTouch(
            itemView.findViewById(R.id.coverLayout)))
}

fun RecyclerView.ViewHolder.elevateSongOnTouch(){
    itemView.setOnTouchListener(ElevateSongOnTouch(
            itemView, itemView.findViewById(R.id.coverLayout)))
}