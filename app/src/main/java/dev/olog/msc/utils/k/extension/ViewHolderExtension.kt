package dev.olog.msc.utils.k.extension

import android.view.MotionEvent
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseModel
import dev.olog.msc.presentation.base.adapter.AdapterDataController
import dev.olog.msc.presentation.utils.animation.ScaleInOnTouch
import dev.olog.msc.presentation.utils.animation.ScaleMoreInOnTouch

fun <T: BaseModel> androidx.recyclerview.widget.RecyclerView.ViewHolder.setOnMoveListener(
        controller: AdapterDataController<T>,
        touchHelper: ItemTouchHelper?
){
    this.itemView.findViewById<View>(R.id.dragHandle)?.setOnTouchListener { _, event ->
        when (event.actionMasked){
            MotionEvent.ACTION_DOWN -> {
                touchHelper?.let {
                    controller.pauseObservingData()
                    touchHelper.startDrag(this)
                    return@setOnTouchListener true
                } ?: return@setOnTouchListener false
            }
            MotionEvent.ACTION_UP -> {
                if (touchHelper != null){
                    controller.resumeObservingData(false)
                }
                false
            }
            else -> false
        }
    }
}

fun <T: BaseModel> androidx.recyclerview.widget.RecyclerView.ViewHolder.setOnClickListener(
        data: AdapterDataController<T>,
        func: (item: T, position: Int, view: View) -> Unit){

    this.itemView.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION){
            data.getItem(adapterPosition)?.let { model -> func(model, adapterPosition, it) }
        }
    }
}

fun <T: BaseModel> androidx.recyclerview.widget.RecyclerView.ViewHolder.setOnClickListener(
        @IdRes resId: Int,
        data: AdapterDataController<T>,
        func: (item: T, position: Int, view: View) -> Unit){

    this.itemView.findViewById<View>(resId)?.setOnClickListener {
        if (adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION){
            data.getItem(adapterPosition)?.let { model -> func(model, adapterPosition, it) }
        }
    }
}

fun <T: BaseModel> androidx.recyclerview.widget.RecyclerView.ViewHolder.setOnLongClickListener(
        data: AdapterDataController<T>,
        func: (item: T, position: Int, view: View) -> Unit){

    itemView.setOnLongClickListener inner@ {
        if (adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION){
            data.getItem(adapterPosition)?.let { model -> func(model, adapterPosition, it) } ?: return@inner false
            return@inner true
        }
        false
    }
}

fun androidx.recyclerview.widget.RecyclerView.ViewHolder.elevateAlbumOnTouch(){
    itemView.setOnTouchListener(ScaleMoreInOnTouch(itemView))
}

fun androidx.recyclerview.widget.RecyclerView.ViewHolder.elevateSongOnTouch(){
    val viewToAnimate = itemView.findViewById<View>(R.id.root)?.let { it } ?: itemView
    itemView.setOnTouchListener(ScaleInOnTouch(viewToAnimate))
}