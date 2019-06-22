package dev.olog.presentation.base

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.model.BaseModel

fun <T: BaseModel> RecyclerView.ViewHolder.setOnClickListener(
    data: ObservableAdapter<T>,
    func: (item: T, position: Int, view: View) -> Unit){

    this.itemView.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION){
            data.getItem(adapterPosition)?.let { model -> func(model, adapterPosition, it) }
        }
    }
}

fun <T: BaseModel> RecyclerView.ViewHolder.setOnClickListener(
    @IdRes resId: Int,
    data: ObservableAdapter<T>,
    func: (item: T, position: Int, view: View) -> Unit){

    this.itemView.findViewById<View>(resId)?.setOnClickListener {
        if (adapterPosition != RecyclerView.NO_POSITION){
            data.getItem(adapterPosition)?.let { model -> func(model, adapterPosition, it) }
        }
    }
}

fun <T: BaseModel> RecyclerView.ViewHolder.setOnLongClickListener(
    data: ObservableAdapter<T>,
    func: (item: T, position: Int, view: View) -> Unit){

    itemView.setOnLongClickListener inner@ {
        if (adapterPosition != RecyclerView.NO_POSITION){
            data.getItem(adapterPosition)?.let { model -> func(model, adapterPosition, it) } ?: return@inner false
            return@inner true
        }
        false
    }
}