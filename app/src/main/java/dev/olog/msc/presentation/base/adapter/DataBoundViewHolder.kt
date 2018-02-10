package dev.olog.msc.presentation.base.adapter

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

class DataBoundViewHolder<out T : ViewDataBinding>(val binding: T)
    : RecyclerView.ViewHolder(binding.root)