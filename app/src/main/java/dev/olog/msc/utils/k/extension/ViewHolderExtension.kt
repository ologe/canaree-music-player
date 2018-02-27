package dev.olog.msc.utils.k.extension

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseModel
import dev.olog.msc.presentation.base.adapter.AdapterDataController
import dev.olog.msc.presentation.utils.animation.ScaleInOnTouch
import dev.olog.msc.presentation.utils.animation.ScaleMoreInOnTouch

fun <T: BaseModel> RecyclerView.ViewHolder.setOnClickListener(
        data: AdapterDataController<T>,
        func: (item: T, position: Int, view: View) -> Unit){

    this.itemView.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data.getItem(adapterPosition), adapterPosition, it)
        }
    }
}

fun <T: BaseModel> RecyclerView.ViewHolder.setOnClickListener(
        @IdRes resId: Int,
        data: AdapterDataController<T>,
        func: (item: T, position: Int, view: View) -> Unit){

    this.itemView.findViewById<View>(resId)?.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data.getItem(adapterPosition), adapterPosition, it)
        }
    }
}

fun <T: BaseModel> RecyclerView.ViewHolder.setOnLongClickListener(
        data: AdapterDataController<T>,
        func: (item: T, position: Int, view: View) -> Unit){

    itemView.setOnLongClickListener inner@ {
        if (adapterPosition != RecyclerView.NO_POSITION){
            func(data.getItem(adapterPosition), adapterPosition, it)
            return@inner true
        }
        false
    }
}

fun RecyclerView.ViewHolder.elevateAlbumOnTouch(){
    itemView.setOnTouchListener(ScaleMoreInOnTouch(itemView))
}

fun RecyclerView.ViewHolder.elevateSongOnTouch(){
    val viewToAnimate = itemView.findViewById<View>(R.id.root)?.let { it } ?: itemView
    itemView.setOnTouchListener(ScaleInOnTouch(viewToAnimate))
}