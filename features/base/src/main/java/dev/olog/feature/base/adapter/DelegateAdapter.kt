package dev.olog.feature.base.adapter

import androidx.recyclerview.widget.RecyclerView

interface DelegateAdapter<VH : RecyclerView.ViewHolder> {

    val delegate: RecyclerView.Adapter<VH>

}